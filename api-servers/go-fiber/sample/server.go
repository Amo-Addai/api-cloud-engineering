package main

import (
	"fmt"
	"log"
	"regexp"
	"io/ioutil"
	"net/http"
	"text/template"
	"html/template"
)

//	HTTP SERVER

type Page struct {
    Title string
    Body  []byte
}
func (p *Page) save() error {
    filename := p.Title + ".txt"
    return ioutil.WriteFile(filename, p.Body, 0600)
}
func loadPage(title string) (*Page, error) {
    filename := title + ".txt"
    body, err := ioutil.ReadFile(filename)
    if err != nil {
        return nil, err
    }
    return &Page{Title: title, Body: body}, nil
}
func testPage() {
    p1 := &Page{Title: "TestPage", Body: []byte("This is a sample Page.")}
    p1.save()
    p2, _ := loadPage("TestPage")
    fmt.Println(string(p2.Body))
}
// 
var templates = template.Must(template.ParseFiles("save.html", "edit.html", "view.html"))
func renderTemplate(w http.ResponseWriter, tmpl string, p *Page) {
	// YOU CAN RENDER TEMPLATES THIS WAY
	err := templates.ExecuteTemplate(w, "templates/" + tmpl + ".html", p)
    // OR, YOU CAN RATHER DO IT THIS WAY ..
    /* t, err := template.ParseFiles("templates/" + tmpl + ".html")
    if err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }
	err = t.Execute(w, p) */
	// 
    if err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
    }
}
// 
var validPath = regexp.MustCompile("^/(edit|save|view)/([a-zA-Z0-9]+)$")
func getTitle(w http.ResponseWriter, r *http.Request) (string, error) {
    m := validPath.FindStringSubmatch(r.URL.Path)
    if m == nil {
        http.NotFound(w, r)
        return "", errors.New("Invalid Page Title")
    }
    return m[2], nil // The title is the second subexpression.
}
// 
func viewHandler(w http.ResponseWriter, r *http.Request) {
	title, err := getTitle(w, r)
	if err != nil { // TRY TO LOG THE err ..
        return
    }
    p, err := loadPage(title)
    if err != nil {
        http.Redirect(w, r, "/edit/" + title, http.StatusFound)
        return
    }
	// fmt.Fprintf(w, "<h1>%s</h1><div>%s</div>", p.Title, p.Body)
	renderTemplate(w, "view", p)
}
func editHandler(w http.ResponseWriter, r *http.Request) {
    title, err := getTitle(w, r)
	if err != nil { // TRY TO LOG THE err ..
        return
    }
    p, err := loadPage(title)
    if err != nil {
        p = &Page{Title: title}
    }
    // fmt.Fprintf(w, "<h1>Editing %s</h1>"+
    //     "<form action=\"/save/%s\" method=\"POST\">"+
    //     "<textarea name=\"body\">%s</textarea><br>"+
    //     "<input type=\"submit\" value=\"Save\">"+
    //     "</form>",
	// 	p.Title, p.Title, p.Body)
	renderTemplate(w, "edit", p)
}
func saveHandler(w http.ResponseWriter, r *http.Request) {
    title, err := getTitle(w, r)
	if err != nil { // TRY TO LOG THE err ..
        return
    }
    body := r.FormValue("body")
    p := &Page{Title: title, Body: []byte(body)}
    err := p.save()
    if err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }
    http.Redirect(w, r, "/view/" + title, http.StatusFound)
}

// OR, YOU CAN USE THIS TYPE OF REQUEST HANDLERS ..

type FuncType func(http.ResponseWriter, *http.Request)
func ErrorHandler (funct FuncType) FuncType {
	return func (w http.ResponseWriter, r *http.Request) {
		defer func(){ // defer THIS FUNC TO RUN AT THE END ..
			if x := recover(); x != nil { // WILL RUN IF THERE'S ANY ERROR ..
				log.Printf("SOME ERROR HAS OCCURED HERE .. ")
				log.Printf(x)
			}
		}()
		funct(w, r) // NOW, YOU CAN CALL YOUR ACTUAL HANDLER funct
	}
}
func sampleHandlerFunction(w http.ResponseWriter, r *http.Request){
	// USE .Fprint() OR .Fprintf() TO echo OUT HTTP RESPONSE TO THIS HTTP REQUEST ..
	fmt.Fprint(w, "this is output in response") // YOU CAN PUT html CODE IN THE STRING ..
	// 
	// 		OR, YOU CAN TRY THIS
	// 
	io.WriteString(w, "<h1> Hello, World ! </h1>")
	// 
	// 		OR, HANDLING A POST REQUEST ..
	//
	w.Header().Set("Content-Type", "text/html")
	switch r.Method {
		case "GET":
			io.WriteString(w, "<html>... <form> ... </form> ...</html>")
		case "POST":
			r.ParseForm() // FIND OUT WHAT THIS DOES, AND IF IT SHOULD RETURN ANY VALUE ..
			io.WriteString(w, r.Form["name ATTRIBUTE OF <input>"][0])
			io.WriteString(w, r.FormValue("name ATTRIBUTE TOO"))
	}

	// http.Redirect(w, r, "url/path", http.StatusFound)
	// res, err := http.Get("url")
	// if err != nil {
	// 	data, err := ioutil.ReadAll(res.Body)
	// 	if err != nil {
	// 		fmt.Print(string(data))
	// 	}
	// }

}


func main() {
	fmt.Print("ABOUT TO START THIS GOLANG SERVER ..")

	// 
	http.HandleFunc("/view/", viewHandler)
    http.HandleFunc("/edit/", editHandler)
	http.HandleFunc("/save/", saveHandler)
	// OR, YOU CAN USE EITHER OF THESE 2 ..
	// http.Handle("/", http.HandleFunc(ErrorHandler(sampleHandlerFunction)))
    http.HandleFunc("/", ErrorHandler(sampleHandlerFunction))


	// 		EXTRA STUFF CAN COME RIGHT HERE ...


	//			NOW, RUN THE SERVER ..
	/*	http.listenAndServe("localhost:8080", nil)
		http.listenAndServerTLS("localhost:8080", nil) 
		log.Fatal(http.ListenAndServe(":8080", nil))	*/
	if err := http.listenAndServe("localhost:8080", nil); err != nil {
		// THEN HANDLE err RIGHT HERE ..
		fmt.Print("SOME ERROR HAS OCCURRED ..")
		panic(err)
	}
	/*	NOW, RECOMPILE THE CODE - go build server.go
		THEN, YOU CAN RUN THE APP FILE - ./server.go
	*/
}





/*
//  TCP SERVER

listener, err := net.List("tcp", "localhost:5000")
for {
	conn, err := listener.Accept()
	go doServerFunction(conn)
}

func doServerFunction(conn net.Conn){
	for {
		buf := make([]byte, 512)
		_, err := conn.Read(buf)
	}
}

//  TCP CLIENT

conn, err := net.Dial("tcp", "localhost:5000")
read := bufio.NewReader(os.Stdin)
client, _ := read.ReadString("\n")
client = strings.Trim(client, "\r\n") // NOW, SEND THE INFO TO THE SERVER ..
for {
	input, _ := read.ReadString("\n") // READ FROM CLIENT
	input = strings.Trim(input, "\r\n") // TRIM ALL THE INPUT STRINGS ..
	_, err := conn.Write([]byte(client + " says: " + input))
}
*/
