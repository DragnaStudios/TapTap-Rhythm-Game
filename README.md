# 🎵 TapTap - Juego de Ritmo

**Proyecto Semestral - Programación I**  
Universidad Tecnológica de Panamá  
Facultad de Ingeniería en Sistemas Computacionales

## 📋 Información del Proyecto

- **Estudiante**: Robert Pimentel (7-714-1252)
- **Grupo**: 1SF122
- **Facilitador**: Rodrigo Yángüez
- **Fecha**: 1 de Agosto de 2025

## 🎮 Descripción

TapTap es un videojuego de ritmo desarrollado en Java utilizando el framework LibGDX. El juego permite a los usuarios disfrutar de una experiencia musical interactiva donde deben tocar notas al ritmo de diferentes canciones.

### ✨ Características Principales

- 🎵 **Sistema de Gestión Musical**: Biblioteca completa de ritmos con metadatos ricos
- 🎮 **Motor de Gameplay Avanzado**: Sistema de 4 carriles con detección de precisión
- 🛠️ **Editor de Beatmaps**: Herramienta profesional para crear mapas de ritmo
- 🎨 **Sistema Visual Avanzado**: Animaciones fluidas y efectos visuales
- 📊 **Sistema de Puntuación**: Evaluación precisa con sistema de combo

### 🎯 Tipos de Notas

- **TAP**: Notas simples que requieren un toque preciso
- **HOLD**: Notas sostenidas que requieren mantener la tecla presionada

## 🚀 Instalación y Ejecución

### Requisitos del Sistema
- Java 8 o superior
- Sistema operativo: Windows, macOS o Linux
- Espacio en disco: 100 MB mínimo
- Memoria RAM: 512 MB mínimo

### Ejecutar desde Código Fuente

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/[tu-usuario]/TapTap.git
   cd TapTap
   ```

2. **Ejecutar el juego**:
   
   **Windows**:
   ```cmd
   gradlew.bat lwjgl3:run
   ```
   
   **Linux/Mac**:
   ```bash
   ./gradlew lwjgl3:run
   ```

### Importar en IntelliJ IDEA

1. Abrir IntelliJ IDEA
2. File → Open → Seleccionar la carpeta del proyecto TapTap
3. IntelliJ detectará automáticamente que es un proyecto Gradle
4. Esperar a que se descarguen las dependencias
5. Ejecutar desde: `lwjgl3` → `src/main/java` → `Lwjgl3Launcher.java`

## 🎮 Controles del Juego

### Gameplay
- **A, S, D, F**: Tocar notas en los carriles correspondientes
- **ESC**: Pausar/despausar o regresar al menú

### Editor de Beatmaps
- **Click Izquierdo**: Colocar nota
- **Click Derecho**: Eliminar nota
- **T/G**: Navegar en el tiempo
- **SPACE**: Reproducir/pausar música
- **1/2**: Cambiar entre modo TAP y HOLD
- **S**: Guardar beatmap

### Navegación General
- **↑/↓ o W/S**: Navegar entre opciones
- **ENTER**: Seleccionar opción

## 📁 Estructura del Proyecto

```
TapTap/
├── core/                    # Lógica principal del juego
│   └── src/main/java/com/game/taptap/
│       ├── TapTapGame.java             # Clase principal
│       ├── Main.java                   # Punto de entrada
│       ├── screens/                    # Pantallas del juego
│       ├── gameplay/                   # Lógica de juego
│       ├── manager/                    # Gestores del sistema
│       ├── model/                      # Modelos de datos
│       └── utils/                      # Utilidades
├── lwjgl3/                  # Configuración de escritorio
├── assets/                  # Recursos del juego
├── build.gradle            # Configuración principal de Gradle
├── settings.gradle         # Configuración de módulos
└── README.md              # Este archivo
```

## 🎵 Canciones Incluidas

- **Doom Slayer** - Mick Gordon - The Only Thing They Fear Is You
- **FF16** - Final Fantasy XVI OST
- **Mashle** - Serious Steel
- **I Love Rock 'N Roll**
- **Pokemon Zinnia Battle**
- **Radagon of the Golden Order**
- **Solo Leveling OST**
- **Spamton Neo**
- **Spider-Man Into the Spider-Verse**
- **Undertale Sans**

## 🏗️ Arquitectura Técnica

### Tecnologías Utilizadas
- **Framework**: LibGDX 1.12.0+
- **Lenguaje**: Java 8+
- **Build System**: Gradle
- **Audio**: LibGDX Audio API
- **Gráficos**: OpenGL 2.0+
- **Fuentes**: FreeType

### Patrones de Diseño Implementados
- **Screen Pattern**: Gestión de pantallas
- **Singleton**: Para TapTapGame
- **Factory**: Creación de notas
- **Observer**: Eventos de input
- **Manager**: Gestión de assets y ritmos

## 📊 Características Técnicas

### Sistema de Timing
- **PERFECTO**: ±50ms del tiempo exacto
- **GRANDE**: ±120ms del tiempo exacto
- **BUENO**: ±180ms del tiempo exacto
- **FALLO**: Fuera de rango o nota perdida

### Sistema de Configuración
- Configuración dinámica de layouts mediante `RhythmSelectConfig`
- Personalización de colores RGB por elemento
- Control granular de animaciones y transiciones

## 🚧 Desarrollo

### Comandos Gradle Útiles
```bash
# Ejecutar el juego
./gradlew lwjgl3:run

# Compilar el proyecto
./gradlew build

# Limpiar build
./gradlew clean

# Crear JAR ejecutable
./gradlew lwjgl3:dist

# Generar archivos para IntelliJ
./gradlew idea
```

## 🎓 Objetivos Académicos Cumplidos

- ✅ Programación orientada a objetos avanzada
- ✅ Integración de frameworks externos (LibGDX)
- ✅ Gestión de archivos y serialización JSON
- ✅ Manejo de excepciones robusto
- ✅ Interfaz gráfica interactiva
- ✅ Arquitectura modular y escalable
- ✅ Documentación técnica completa

---

**Universidad Tecnológica de Panamá**  
*Ingeniería de Software - Programación I*  
*Panamá, 2025*
