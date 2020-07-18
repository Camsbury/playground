package main

import (
	"fmt"
	"log"
	"net/http"
	"time"
)

func main() {
	resp, _ := http.Get("https://www.httpbin.org")
	var wait time.Duration
	wait = 500
	time.Sleep(wait * time.Millisecond)
	log.Fatal("hi")
	fmt.Println(resp)
}
