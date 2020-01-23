# Udash Developer's Guide

This guide provides knowledge essential for implementing web applications with the Udash framework. The quick start 
guide below will introduce you to Udash basics.

## Quick start guide

A good starting point is a generation of a base project with 
the [Giter8](http://www.scala-sbt.org/1.x/docs/sbt-new-and-Templates.html) project generator. The generator is 
a built-in SBT mechanism since the `0.13.13` version.

To start a new project just type: `sbt new UdashFramework/udash.g8`.

The generator allows you to customize some basic properties of the target project. 
The generated sources contain comprehensive READMEs which provide guidance around the code and links 
to useful sources of knowledge about development with Udash.

To compile and start the generated project run the following command inside the generated directory: 
`sbt compileStatics run`.

### What's inside?

The generator creates a simple chat application, which showcases the most important features of the Udash framework. 
It uses properties, Bootstrap components, RPC with notifications from the server, translations and more.

When you open the application in your browser you should see a login page form. In the top-right corner 
you can change the page language. Below you can type user credentials and go to the chat window.

On the chat view you can type, send and read messages. You should try to open another browser window and check 
that messages and connections count refresh automatically. Server notifies authenticated clients about these events 
via Server -> Client RPC notification.

This demo presents the usage of the other useful tools for building and deploying web applications. 
In the backend module the application uses Jetty and Spring to setup the server. 
Each module contains tests based on ScalaTest and ScalaMock. The frontend and shared modules use 
*scalajs-env-selenium* in order to run the tests compiled to JavaScript in a web browser. 
The sbt configuration employs *sbt Native Packager* to provide easy deployment process.

## What's next?

It's time to learn the basics of the Udash framework. You can learn more about the Udash project configuration 
in the [Udash bootstrapping](/bootstrapping) chapter. Check also 
[Frontend application development](/frontend) and [RPC in Udash](/rpc). 