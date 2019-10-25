# ecore-glsp
[![Build Status](https://travis-ci.org/eclipsesource/ecore-glsp.svg?branch=master)](https://travis-ci.org/eclipsesource/ecore-glsp)

Ecore GLSP provides a web-based editor for Ecore Models (including Diagrams), integrated with Eclipse Theia. It contains two components: one [GLSP](https://github.com/eclipsesource/glsp) language server (Server-side, written in Java), and one GLSP client extension to actually present the diagrams (Using [Sprotty](https://github.com/eclipse/sprotty-theia)). 

Ecore GLSP can display an existing Ecore model. The diagram layout will be persisted in an .enotation file next to the .ecore file. The diagram editor also supports creation of new elements (EClasses, EAttributes, EReferences...), as well as partial support for editing existing elements (Renaming, deleting...).

![Ecore GLSP Example](images/example.png)

Server \
Note: to build and run the Ecore GLSP Server, you need Java with version >= 11.

* Build:
  * cd server
  * mvn install -U

* Run:
  * Execute the Java main class: com.eclipsesource.glsp.ecore.EcoreServerLauncher

Client
 * Follow instructions on /client [Readme file](https://github.com/eclipsesource/ecore-glsp/blob/master/client/README.md)
