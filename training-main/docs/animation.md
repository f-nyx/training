# Animation engine

This document describes the ASCII animation engine architecture.

## Coordinate System

The coordinate system is based on the coordinate system of the display device. The basic unit of measure is the pixel.
Points on the screen are described by x- and y-coordinate pairs. The x-coordinates increase to the right; y-coordinates
increase from top to bottom. The origin (0,0) for the system is located at top left of the screen.

m /---------------\
  |               |
  |               |
  \---------------/
  Figure 1. The point m on the screen represents the
  coordinate system origin (x = 0, y = 0)

All geometry in the engine will use this coordinate system.