#!/bin/bash
#converts the file $1 to a string of bits saved to $2
xxd -b "$1" | cut -d" " -f 2-7 | tr -d "\n " > "$2"
