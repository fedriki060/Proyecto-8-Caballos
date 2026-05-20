# ♞ Problema de los 8 Caballos — Backtracking con Aleatoriedad

> Colocación de 8 caballos en un tablero 8×8 sin que ninguno ataque a otro,  
> resuelto mediante backtracking con exploración aleatoria en Java.

---

## Descripción del Problema

El problema consiste en ubicar exactamente **8 caballos** en un tablero de ajedrez de 8×8 de modo que **ningún caballo pueda atacar a otro**. El caballo se mueve en forma de "L": 2 casillas en una dirección y 1 en la perpendicular.

Formalmente, dado el conjunto de posiciones S = {(f₁,c₁), ..., (f₈,c₈)}, para todo par (fᵢ,cᵢ) y (fⱼ,cⱼ) con i ≠ j, la diferencia (|fᵢ−fⱼ|, |cᵢ−cⱼ|) **no puede ser** (1,2) ni (2,1).

---

## Modos de Uso

El programa soporta dos modos de inicio:

| Modo | Método | Descripción |
|------|--------|-------------|
| **Tablero vacío** | `resolverVacio()` | El algoritmo parte desde cero y ubica los 8 caballos |
| **Caballo inicial** | `resolverConInicial(fila, col)` | El usuario fija una posición; el algoritmo coloca los 7 restantes |

```java
ColocacionCaballos solver = new ColocacionCaballos();

// Caso 1 — tablero vacío
solver.resolverVacio();

// Caso 2 — caballo inicial en (2, 3)
solver.resolverConInicial(2, 3);

solver.imprimirTablero();
```

---

## Estructura del Proyecto

```
src/
└── Backtracking/
    └── ColocacionCaballos.java   ← clase principal
```

### Clase `ColocacionCaballos`

| Componente | Descripción |
|------------|-------------|
| `int[][] tablero` | Matriz 8×8 — `0` vacío · `1` caballo · `2` caballo inicial fijo |
| `Random rng` | Fuente de aleatoriedad para el shuffle |
| `MOVIMIENTOS_CABALLO` | Los 8 deltas en L del caballo (constante estática) |
| `resolverVacio()` | Reinicia el tablero y lanza `colocarCaballosAleatorio(0)` |
| `resolverConInicial(f, c)` | Valida entrada, marca posición con `2`, lanza `colocarCaballosAleatorio(1)` |
| `colocarCaballosAleatorio(n)` | Núcleo recursivo con backtracking y orden aleatorio |
| `esSegura(f, c)` | Verifica que la casilla esté vacía y no sea atacada |
| `imprimirTablero()` | Imprime el tablero final en consola |

---

## Formulación de la Solución

### Estado del tablero

```
0  →  casilla vacía
1  →  caballo colocado por el algoritmo
2  →  caballo inicial fijo (Caso 2)
```

### ¿Por qué Backtracking?

El espacio de búsqueda tiene **C(64,8) ≈ 4,426,165,368** combinaciones posibles. Backtracking descarta ramas completas en cuanto detecta un conflicto, reduciendo drásticamente el trabajo real.

El patrón es clásico:

```
colocar → verificar → recursar
              ↓ falla
           deshacer → probar siguiente
```

### Aleatoriedad controlada

A diferencia de un backtracking determinista (orden fila por fila), esta implementación **baraja las 64 posiciones** antes de cada llamada recursiva:

```java
List<int[]> posiciones = new ArrayList<>();
for (int f = 0; f < 8; f++)
    for (int c = 0; c < 8; c++)
        posiciones.add(new int[]{f, c});

Collections.shuffle(posiciones, rng);  // orden diferente cada vez

for (int[] pos : posiciones) {
    if (esSegura(pos[0], pos[1])) {
        tablero[pos[0]][pos[1]] = 1;
        if (colocarCaballosAleatorio(n + 1)) return true;
        tablero[pos[0]][pos[1]] = 0;  // backtrack
    }
}
```

Esto garantiza que **cada ejecución produzca una solución distinta** sin sacrificar completitud: si existe una solución, el algoritmo la encuentra.

---

## Restricciones y Validaciones

### Posición segura — `esSegura(fila, col)`

Una casilla es válida si y solo si:

1. Está vacía: `tablero[fila][col] == 0`
2. Ninguno de los 8 saltos del caballo desde ella apunta a una casilla ocupada (`!= 0`), cubriendo tanto caballos normales (`1`) como el caballo inicial fijo (`2`)

```java
for (int[] mov : MOVIMIENTOS_CABALLO) {
    int fa = fila + mov[0];
    int ca = col  + mov[1];
    if (fa >= 0 && fa < 8 && ca >= 0 && ca < 8)
        if (tablero[fa][ca] != 0) return false;
}
return true;
```

### Validación de entrada — `resolverConInicial`

```java
if (fila < 0 || fila >= 8 || col < 0 || col >= 8)
    throw new IllegalArgumentException("Posición fuera del tablero");
```

### Caso base

```java
if (caballosColocados == 8) return true;
```

La condición se evalúa **antes** de intentar colocar otro caballo, garantizando que nunca se intente un noveno.

---

## Los 8 Movimientos del Caballo

```
(-2,-1)  (-2,+1)
(-1,-2)  (-1,+2)
(+1,-2)  (+1,+2)
(+2,-1)  (+2,+1)
```

---

## Ejemplo de Salida

```
=== TABLERO FINAL ===
. . . C . . . .
. . . . . C . .
. C . . . . . C
. . . . C . . .
C . . . . . C .
. . C . . . . .
. . . . . . . .
. . . . . C . .
```

`C` = caballo colocado · `.` = casilla vacía

---

