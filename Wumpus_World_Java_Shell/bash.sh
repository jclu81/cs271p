#!/bin/bash
    
    for i in `ls Worlds/`;
    do
            java -jar bin/Wumpus_World.jar Worlds/$i >> a.txt;
            echo $i >> a.txt;
    done 