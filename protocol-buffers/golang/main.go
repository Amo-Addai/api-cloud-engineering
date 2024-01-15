package main

import (
	"fmt"
	"log"
	"io/ioutil"

	"github.com/golang/protobuf/proto"
	"github.com/golang/protobuf/jsonpb"

	// TODO: uncomment these when you correctly setup your custom repository
	// "github.com/{USER_NAME}/{REPO_NAME}/protocol-buffers/golang/src/simple"
	// "github.com/{USER_NAME}/{REPO_NAME}/protocol-buffers/golang/src/enum"
	// "github.com/{USER_NAME}/{REPO_NAME}/protocol-buffers/golang/src/complex"
)

func main() {
	fmt.Println("Hello World!")

	sm := doSimple()
	readAndWriteDemo(sm)
	jsonDemo(sm)

	doEnum()
	doComplex()
}

func doSimple() *simplepb.SimpleMessage {
	sm := simplepb.SimpleMessage {
		Id: 		123,
		IsSimple: 	true,
		Name: 		"Simple Message Name",
		SampleList: []int32{1, 4, 7},
	}
	fmt.Println(sm)
	return &sm
}

func readAndWriteDemo(sm proto.Message) {
	writeToFile("simple.bin", sm)
	sm2 := &simplepb.SimpleMessage{}
	readFromFile("simple.bin", sm2)

	fmt.Println(sm2)
}

func jsonDemo(sm proto.Message) {
	smStr := toJSON(sm)
	fmt.Println(smStr)
	sm2 := &simplepb.SimpleMessage{}
	fromJSON(smStr, sm2)
	fmt.Println(sm2)
}

func writeToFile(fname string, pb proto.Message) error {
	out, err := proto.Marshal(pb)
	if err != nil {
		log.Fatalln("Can't serialize to bytes", err)
		return err
	}
	if err := ioutil.WriteFile(fname, out, 0644); err != nil {
		log.Fatalln("Can't write to file", err)
		return err
	}
	fmt.Println("Data has been written")
	return nil
}

func readFromFile(fname string, pb proto.Message) error {
	in, err := ioutil.ReadFile(fname)
	if err != nil {
		log.Fatalln("Can't read from file", err)
		return err
	}
	if err2 := proto.Unmarshal(in, pb); err2 != nil {
		log.Fatalln("Can't deserialize from bytes")
		return err2
	}
	return nil
}

func toJSON(pb proto.Message) string {
	marshaler := jsonpb.Marshaler{}
	out, err := marshaler.MarshalToString(pb)
	if err != nil {
		log.Fatalln("Can't convert to JSON", err)
		return ""
	}
	return out
}

func fromJSON(in string, pb proto.Message) {
	if err := jsonpb.UnmarshalString(in, pb); err != nil {
		log.Fatalln("Can't convert from JSON", err)
	}
}

func doEnum() {
	em := enumpb.EnumMessage {
		Id:			47,
		DayOfWeek:	enumpb.DayOfWeek_MONDAY,
	}
	fmt.Println(em)
}

func doComplex() {
	cm := complexpb.ComplexMessage {
		OneSample: &complexpb.SampleMessage {
			Id:			47,
			Name:		"Complex Message Name",
			IsComplex:	true,
		},
		MultipleSamples: []*complexpb.SampleMessage {
			&complexpb.SampleMessage {
				Id:			1,
				Name:		"Next Message Name",
				IsComplex:	true,
			},
			&complexpb.SampleMessage {
				Id:			2,
				Name:		"Next Message Name",
				IsComplex:	true,
			},
			&complexpb.SampleMessage {
				Id:			3,
				Name:		"Next Message Name",
				IsComplex:	true,
			},
		},
	}
	fmt.Println(cm)
}