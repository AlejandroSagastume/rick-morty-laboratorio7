# Guía de Estudio y Defensa: Laboratorio 8 (Rick and Morty App)

Esta guía explica detalladamente **qué se agregó**, **cómo se implementó** y **por qué se hizo así**, diseñada específicamente para que puedas responder cualquier pregunta de tu profesor o evaluador el día de mañana.

---

## 1. Conceptos Clave Implementados

### A. Navegación Segura por Tipos (Type-safe Navigation con `@Serializable`)
* **¿Qué es?** Es el estándar moderno en Jetpack Compose (Navigation 2.8.0+) donde las rutas de navegación ya no son simples cadenas de texto (Strings como `"details/5"`), sino **clases u objetos de Kotlin anotados con `@Serializable`** (de la librería `kotlinx.serialization`).
* **¿Por qué se usa?**
  * **Seguridad en tiempo de compilación:** Si cambias un parámetro en la clase, el compilador te avisa si hay errores en otra parte, evitando fallos en tiempo de ejecución (Type safety).
  * **Paso de parámetros limpio:** Puedes pasar objetos o tipos primitivos directamente en el constructor de la clase de ruta (ej. `CharacterDetails(val id: Int)`).

### B. Navegación Anidada (Nested Navigation)
* **¿Qué es?** Consiste en agrupar un conjunto de pantallas relacionadas dentro de su propio subgrafo de navegación (`NavGraph`), el cual pertenece a su vez a un gráfico principal.
* **¿Cómo se aplicó en este laboratorio?**
  * Se crearon dos subgrafos principales en el `MainShell`:
    1. **`CharactersGraph`**: Agrupa la lista de personajes (`CharactersList`) y el detalle del personaje (`CharacterDetails`).
    2. **`LocationsGraph`**: Agrupa la lista de locaciones (`LocationsList`) y el detalle de la locación (`LocationDetails`).
  * Esto permite que cada pestaña del Bottom Navigation mantenga su propio historial de navegación (backstack) de forma independiente.

### C. Paso de datos mínimos (Solo el ID)
* **¿Cómo funciona?** Al hacer clic en un elemento de la lista (personaje o locación), **únicamente se navega pasando el ID** (ej. `navController.navigate(LocationDetails(location.id))`).
* En la pantalla de detalles (`LocationDetailsScreen`), se captura el ID usando `entry.toRoute<LocationDetails>().id` y se consulta la fuente de datos local (`LocationDb`) para obtener la información completa. Esto evita pasar objetos pesados o serializar estructuras grandes a través del navegador.

---

## 2. Archivos Nuevos y Modificados

### 1. `navigation/AppDestination.kt`
* **¿Qué contiene?** Todos los destinos de la aplicación definidos como objetos y clases serializables.
* **Código clave:**
  ```kotlin
  @Serializable object Login
  @Serializable object Main
  @Serializable object CharactersGraph
  @Serializable object CharactersList
  @Serializable data class CharacterDetails(val id: Int)

  @Serializable object LocationsGraph
  @Serializable object LocationsList
  @Serializable data class LocationDetails(val id: Int)

  @Serializable object Profile
  ```

### 2. Modelos y Bases de Datos Locales (`Location.kt` y `LocationDb.kt`)
* **`Location.kt`**: Modelo de datos (`data class Location`) que define los atributos de una locación: `id`, `name`, `type`, `dimension`.
* **`LocationDb.kt`**: Repositorio en memoria que contiene una lista estática de 20 locaciones de Rick and Morty y provee métodos como `getAllLocations()` y `getLocationById(id)`.

### 3. Pantallas de Locaciones y Perfil (`screens/`)
* **`LocationsScreen.kt`**: Muestra una lista vertical (`LazyColumn`) con las locaciones. Cada elemento muestra el **Nombre** (`name`) y el **Tipo** (`type`), tal como pedía la rúbrica, y es clickeable.
* **`LocationDetailsScreen.kt`**: Muestra la información detallada de la locación seleccionada (ID, Nombre, Tipo, Dimensión) con un `TopAppBar` que incluye el título *"Location details"* y el botón de retroceso.
* **`ProfileScreen.kt`**: Muestra la imagen de perfil, nombre completo (*Alejandro Sagastume*), carné (*25257*) y un botón de cerrar sesión.

### 4. Estructura de Navegación Principal (`MainActivity.kt`)
* **`AppNavigation`**: Controla el flujo principal entre la pantalla de `Login` y la pantalla principal (`Main`).
* **`MainShell`**: Contiene el `Scaffold` con la barra de navegación inferior (`NavigationBar` / BottomNavigation) con 3 opciones:
  1. *Characters* -> Navega al subgrafo `CharactersGraph`.
  2. *Locations* -> Navega al subgrafo `LocationsGraph`.
  3. *Profile* -> Navega directo a `Profile`.
* Dentro de `MainShell`, el `NavHost` anida los dos gráficos (`navigation<CharactersGraph>` y `navigation<LocationsGraph>`), manejando el historial de navegación de cada pestaña.

---

## 3. Preguntas Frecuentes que te podrían hacer (y cómo responderlas)

1. **P: ¿Por qué ya no se usan Strings para navegar como antes?**
   * **R:** Porque las rutas basadas en Strings (`navController.navigate("details/5")`) son propensas a errores de dedo (typos) y no garantizan seguridad de tipos. Con `@Serializable` (Type-safe Navigation), el compilador valida los parámetros y las rutas de manera automática y segura.

2. **P: ¿Qué es el Nested Navigation y por qué se usó para Characters y Locations?**
   * **R:** Es la técnica de anidar subgrafos de navegación. Se usó para que al cambiar entre las pestañas del Bottom Navigation (Characters y Locations), cada sección mantenga su propio historial de navegación independiente. Si entras al detalle de un personaje y luego cambias a Locations y regresas a Characters, tu navegación previa no se pierde gracias a `saveState` y `restoreState`.

3. **P: ¿Cómo se pasan los datos a la pantalla de detalles?**
   * **R:** Siguiendo la buena práctica de pasar únicamente el identificador (`id: Int`) como parámetro en el objeto serializable (`LocationDetails(id)`). La pantalla de destino recibe ese ID, consulta la base de datos local (`LocationDb.getLocationById(id)`) y renderiza la información.

4. **P: ¿Cómo funciona el botón de cerrar sesión (Logout)?**
   * **R:** Al presionar "Cerrar sesión" en el perfil, se ejecuta una navegación hacia el destino `Login` limpiando por completo el backstack (`popUpTo(navController.graph.id) { inclusive = true }`), de modo que si el usuario presiona el botón "Back" del teléfono, la aplicación se cierra en lugar de regresar al menú principal.
