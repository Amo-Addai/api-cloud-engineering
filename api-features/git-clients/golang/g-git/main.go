package main

import (
	"fmt"
)

func do(arg string) {
	fmt.Println(arg)
}

func main() {
	var arg string = "G-Git"
	go do(arg)
}


// g-git init / status / commit / log / diff / push / pull / merge
