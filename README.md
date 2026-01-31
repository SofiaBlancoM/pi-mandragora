# Mandrágora – Backoffice 📚

Este proyecto es una **aplicación de backoffice** para la gestión de la librería **Mandrágora**.

La aplicación permite gestionar libros, autores y categorías, así como realizar operaciones típicas de un sistema administrativo (crear, editar, listar, borrar, etc.).  

---

## 🧠 Objetivo del proyecto

Este repositorio sirve como **Proyecto intermodular** para el grado DAM en el Carlos III para prácticar las siguientes _skills_:

- **Clean Architecture** y principios básicos de diseño  
- Aprendan a estructurar proyectos grandes de forma mantenible  
- Añadan nuevas funcionalidades de forma incremental  
- Usar Git correctamente (commits, ramas, evolución del proyecto)
---

## 🧱 Stack tecnológico

- **Java 17**
- **JavaFX**
- **Arquitectura Clean / Hexagonal (adaptada)**
- **Supabase** (Auth, Base de datos y Storage)
- **HTTP Client de Java**
- **Lombok**
- **Maven**

---

## 📁 Estructura general

De forma simplificada:

- `application` → Casos de uso y lógica de negocio  
- `domain` → Entidades y enums del dominio  
- `infrastructure` → Acceso a datos, Supabase, HTTP, storage  
- `presentation` → JavaFX, controllers, view models y navegación  
- `shared` → Kernel compartido entre todas las capas de la aplicación

El objetivo es **separar responsabilidades** y evitar dependencias incorrectas entre capas.

---

## ⚙️ Configuración del proyecto

### 📄 Archivo `local.properties`

Por motivos de seguridad, **el archivo de configuración no está incluido en el repositorio**.

Es obligatorio crear un archivo llamado:

```
local.properties
```

en la ruta:

```
/src/main/resources/config/local.properties
```

Puedes usar como base el archivo `example.properties`.

---

### 🧪 Contenido mínimo del archivo

```properties
supabase.url=
supabase.anonKey=
supabase.storage.bucket=
app.name=
test.autologin.user.email=
test.autologin.user.password=
books.page.size=
images.signedUrl.ttlSeconds=

```

**Notas:**
- Los datos de Supabase son obligatorios para que la app funcione.
- Las credenciales de test se usan solo en desarrollo y deben ser tu usuario y contraseña en supabase para que funcione el botón de _autologin_.
- Los valores de paginación y TTL tienen valores por defecto si no se configuran.

Este archivo **no debe subirse a GitHub**.

---

## ▶️ Ejecución

1. Clonar o importar el proyecto  
2. Crear el archivo `local.properties`  
3. Configurar Java 17  
4. Ejecutar la aplicación desde el `main` correspondiente  
5. (Opcional) Usar el autologin para pruebas rápidas  

---

## 🧑‍🎓 Autoras

- Sofía Blanco Méndez
- Lattifa Michab Bousselham
- Patricia Ruiz Mateo

---

¡Muchas gracias y comprad en Mandrágora! 🌿📚
