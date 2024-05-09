package main

import (
	"fmt"
)

func do(score float64) {
	fmt.Print("A")
}

func main() {
	var score float64
	score = 1000000000000000000.00
	fmt.Print("Hello, World!\n:D")
	go do(score)
}
