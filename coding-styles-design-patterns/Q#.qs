/* // TODO: To-Use

Generics
..

*/


////////////////////////////////////////
//  CODING STYLES
////////////////////////////////////////


////////////////////////////////////////
//  DESIGN PATTERNS
////////////////////////////////////////

// * Creational


// * Structural


// * Behavioral

// * Others



////////////////////////////////////////
//  OTHER PATTERNS
////////////////////////////////////////


////////////////////////////////////////
//  TEST CASES
////////////////////////////////////////

// TODO: Move namespace starting scope to top of file
namespace Quantum.HelloWorld {
    open Microsoft.Quantum.Intrinsic;
    open Microsoft.Quantum.Canon;

    Message("Hello Quantum World!");

    operation HelloQ() : Result {
        using (qubit = Qubit()) {
            H(qubit);
            let result = M(qubit);
            Reset(qubit);
            return result;
        }
    }
}
