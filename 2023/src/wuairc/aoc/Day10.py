# from https://github.com/OskarSigvardsson/adventofcode/blob/master/2023/day10/day10.py
#
# this queue is synchronized, which is pretty pointless...
from queue import Queue

import os

file_path = os.path.dirname(__file__)
input_file = os.path.join(file_path, "../../main/resources/input/10/input.txt")

with open(input_file, "r") as f:
    matrix = [l.strip() for l in f]

    nextDirections = {
        "|": [ ( 0,-1), ( 0, 1) ],
        "-": [ (-1, 0), ( 1, 0) ],
        "L": [ ( 0,-1), ( 1, 0) ],
        "J": [ ( 0,-1), (-1, 0) ],
        "7": [ (-1, 0), ( 0, 1) ],
        "F": [ ( 1, 0), ( 0, 1) ],
    }

    x,y = None, None

    for yi,line in enumerate(matrix):
        for xi,c in enumerate(line):
            if c == "S":
                x,y = xi,yi
                break

    assert(x != None)
    assert(y != None)

    q = Queue()
    
    for dx,dy in [(-1,0),(1,0),(0,-1),(0,1)]:
        c = matrix[y+dy][x+dx]
        if c in nextDirections:
            for dx2,dy2 in nextDirections[c]:
                if x == x+dx+dx2 and y == y+dy+dy2:
                    q.put((1,(x+dx,y+dy)))

    dists = { (x,y): 0 }
    assert(q.qsize() == 2)

    while not q.empty():
        d,(x,y) = q.get()

        if (x,y) in dists:
            continue

        #print(d,(x,y))
        dists[(x,y)] = d

        for dx,dy in nextDirections[matrix[y][x]]:
            q.put((d+1,(x+dx,y+dy)))

    print(f"Part 1: {max(dists.values())}")
    
    w = len(matrix[0])
    h = len(matrix)
    
    inside_count = 0
    for y,line in enumerate(matrix):
        for x,c in enumerate(line):
            if (x,y) in dists:
                continue
            
            crosses = 0
            x2,y2 = x,y

            while x2 < w and y2 < h:
                c2 = matrix[y2][x2]
                if (x2,y2) in dists and c2 != "L" and c2 != "7":
                    crosses += 1
                x2 += 1
                y2 += 1


            if crosses % 2 == 1:
                inside_count += 1

    print(f"Part 2: {inside_count}")
