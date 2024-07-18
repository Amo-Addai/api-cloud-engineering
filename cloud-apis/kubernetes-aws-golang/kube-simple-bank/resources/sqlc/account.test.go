package db

import "testing"

func TestCreateAccount(t *testing.T) {
	arg := createAccountParams{
		Owner:    "tom",
		Balance:  100,
		Currency: "USD",
	}
	account, err := testQueries.CreateAccount()
}
