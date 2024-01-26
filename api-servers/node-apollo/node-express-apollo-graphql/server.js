const express = require('express')
const { graphqlExpress, graphiqlExpress } = require('apollo-server-express');
const { makeExecutableSchema } = require('graphql-tools');
const { ApolloServer, gql } = require('apollo-server');
// 
const bodyParser = require('body-parser');
const path = require('path');
// 

//              FIRST, HANDLE ALL THE GRAPH-QL STUFF HERE ..
// Some fake data
const books = [
    {
      title: "Harry Potter and the Sorcerer's stone",
      author: 'J.K. Rowling',
    },
    {
      title: 'Jurassic Park',
      author: 'Michael Crichton',
    },
  ];
  
  // The GraphQL schema in string form
  const typeDefs = `
    type Query { books: [Book] }
    type Book { title: String, author: String }
  `;
  
  // The resolvers
  const resolvers = {
    Query: { books: () => books },
  };
  
  // Put together a schema
  const schema = makeExecutableSchema({
    typeDefs,
    resolvers,
  });
//   

/* //   USE THIS TO RUN AN APOLLO-SERVER WITHOUT NODE-EXPRESS ..
// In the most basic sense, the ApolloServer can be started by passing type definitions (typeDefs) 
// and the resolvers responsible for fetching the data for those types.
const server = new ApolloServer({ typeDefs, resolvers });
server.listen().then(({ url }) => { console.log(`🚀  Server ready at ${url}`); });
*/

//                      NOW, YOU CAN HANDLE THE ACTUAL NODE-EXPRESS STUFF ..
const app = express();

const port = 3000

// The GraphQL endpoint
app.use('/graphql', bodyParser.json(), graphqlExpress({ schema }));
// GraphiQL, a visual editor for queries
app.use('/graphiql', graphiqlExpress({ endpointURL: '/graphql' }));


app.get('/', (req, res) => res.send('Hello World!'))
app.use('/static', express.static( path.join(__dirname, '../../front-end') ));


// Start the server
app.listen(port, () => console.log(`Example app listening on port ${port}!`))


/* // OR YOU ALSO USE THE http(s) LIBRARIES (BUT USE DIFFERENT PORTS ..)
var https = require('https');
var http = require('http');

http.createServer(app).listen(80);
https.createServer(options, app).listen(443); */