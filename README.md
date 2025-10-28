# Compilador en Java

Compilador que implementa **análisis léxico**, **sintáctico** y **semántico** para el lenguaje **MiniLang**.  
Procesa archivos fuente con extensión **`.min`** y muestra en consola: análsis léxico, sintáctico y semántico, errores de cada fase y tabla de símbolos.

---

## Tabla de Contenidos
1. [Descripción](#descripción)
2. [Características](#características)
3. [Tecnologías utilizadas](#tecnologías-utilizadas)
4. [Estructura del proyecto](#estructura-del-proyecto)
5. [Archivo fuente (`archivo.min`)](#archivo-fuente-archivomin)
6. [Ejecución](#ejecución)

---

## Descripción:

El compilador toma un archivo `.min`, realiza:

1. **Análisis léxico** --> tokeniza el código fuente (palabras reservadas, identificadores, números, símbolos, etc.) y reporta **errores léxicos**.  
4. **Análisis sintáctico** --> valida la **estructura gramatical** y construye un **AST** (árbol sintáctico abstracto) con `Expr` y `Stmt`.  
5. **Análisis semántico** --> verifica **declaraciones**, **uso de variables** y **compatibilidad de tipos**. Muestra **errores semánticos** y la **tabla de símbolos**.

> **Importante:** Para ejecutar desde IntelliJ, se debe configurar la ruta del archivo en *Program arguments*, colocando la siguiente ruta:  
> `src/test/java/minilang/archivo.min`

---

## Características:

- Reconoce comentarios `/* ... */` y `// ...`.
- Palabras reservadas: `long`, `double`, `if`, `then`, `else`, `while`, `break`, `read`, `write`.
- Operadores aritméticos: `+ - * /`.
- Operadores relacionales/lógicos: `> < >= <= == !=` (y `!`, `&&`, `||` opcionales).
- Asignación/agrupación: `= ( ) { } ; ,`.
- Identificadores: letra o `_` seguidos de letras/dígitos/`_`.
- Constantes: enteras, reales, strings, booleanas (`true`/`false`).
- Tabla de símbolos con: nombre y tipo.

---

## Tecnologías utilizadas

- **Java** java 17.0.12 2024-07-16 LTS
- **IntelliJ IDEA** 

---

## Estructura del proyecto

```text
src/
 └── main/
      └── java/
           └── minilang/
                ├── Main.java               # Orquestación: lee .min, corre léxico/sintáctico/semántico
                ├── Lexer.java              # Analizador léxico
                ├── Parser.java             # Analizador sintáctico (AST)
                ├── SemanticAnalyzer.java   # Analizador semántico
                ├── SymbolTable.java        # Tabla de símbolos
                ├── Symbol.java             # Entrada de la TS
                ├── Expr.java               # Nodos de expresiones del AST
                ├── Stmt.java               # Nodos de sentencias del AST
                ├── Token.java              # Modelo de token
                ├── TokenType.java          # Enumeración de tipos de token
                ├── LexError.java           # Error léxico
                ├── SyntaxError.java        # Error sintáctico
                └── SemanticError.java      # Error semántico
 └── test/
      └── java/
           └── minilang/
                ├── archivo.min             # Código fuente a compilar/analizar
                ├── LexerTest.java          # Prueba aislada de Léxico (opcional)
                ├── ParserTest.java         # Prueba aislada de Sintaxis (opcional)
                └── SemanticTest.java       # Prueba aislada de Semántica (opcional)


```
---

## Archivo fuente (archivo.min): 
El archivo archivo.min contiene el programa MiniLang que el compilador analiza.
Ubicación --> src/test/java/minilang/archivo.min
Editá ese archivo para probar distintos casos (con o sin errores).

---

## Ejecución:

Ejecución en IntelliJ IDEA: 
1. Abrí Run → Edit Configurations…
2. En Program arguments, escribí exactamente --> src/test/java/minilang/archivo.min
3. Ejecutá la clase Main (minilang.Main).

El Main imprimirá en consola:
- Tokens (resultado del análisis léxico)
- Errores léxicos (si hay)
- Errores sintácticos (si hay)
- Errores semánticos (si hay)
- Tabla de símbolos

Ejecución por línea de comandos:
Desde la raíz del proyecto, una vez compilado, poner en la terminal el siguinete comando --> java minilang.Main src/test/java/minilang/archivo.min






