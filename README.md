# ex3-protocol-angulate2
## What is this?
This is the Angular2-based frontend to Loom of Characters.

## What is Loom of Characters?
Loom of Characters is a character management tool for the tabletop roleplaying game Exalted Third Edition. It also 
includes alternative

## Where can I get a look and feel?

There is a frontend-only version running at 

https://loom-of-characters-preview.firebaseapp.com

## Development

### How to compile:
The frontend can be compiled by cloning this repository and executing `sbt fastOptJS`.

To fully compile, optimize, and prepare the app for deployment there is a shortcute `sbt completeFull`. It will create a new "web"
directory which can be deployed via static file hosting.

### Architecture, platforms and frameworks:
The whole of Loom of Characters is written in Scala. The server is compiled to JVM Bytecode, while the client (this repository) is 
using Scala.js
and angulate2 to run as a Single-Page-Application within modern browsers.

The whole architecture is based on the CQRS design pattern, and uses Event Sourcing on the server side to persist 
all data in a threadsafe manner.

For authentication the app is currently using the Sign In With Google on the client, extracting OpenID Tokens and 
using them to authenticate all REST calls to the backend server.

The core idea is that the frontend is working offline just as well as online. For now, the frontend files are served with AppCache -
this will be replaced by a ServiceWorker based implementation in the future. The frontend also uses IndexedDB to store aggregate data
which is returned by the server. On the same hand, it applies locally created CQRS commands/events straight away on the 
local copies of the data, instead of waiting for a server roundtrip. If the server disagrees with the changes the client will 
overwrite the local change on the next change accepted by the remote server. Lastly, to fully work offline, all locally created commands
which were not yet sent to the server are also stored in IndexedDB and 
will be resend once connection is reestablishment, preserving the order of all commands.


### Why I even started this:
I wanted to get real experience with CQRS and with modern browser APIs, and I noticed that there is not yet a comparable tool available
for Exalted Third Edition, a point that is bugging me as a regular Exalted ST.
