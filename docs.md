---
layout: default
title: DOCS
permalink: /docs/
---
 
DOCS
====

-------------------------

Table of Contents
-----

- [Installation](#installation)
  - [Windows](#windows)
  - [OS X](#os-x)
- [Usage](#usage)
- [Macro](#macro)
  - [Global Variables](#global-variables)
  - [Macro API](#macro-api)

--------------------------

## Installation

### Windows


### OS X

--------------------------

## Usage

--------------------------

## Macro

You can automate series of tasks by writing macros.
Macros are written in JavaScript and executed in SmoothCSV Application.

Macros would be executed through [Rhino](https://www.mozilla.org/rhino/), which is an implementation of JavaScript written in Java.

There are basically 2 ways to create macros.

- Record keys  
  You can record series of keyboard commands by selecting the menu `Start Recording Macro`.
  Then select `Stop Recording Macro` to stop recording. It will generate JavaScript code and you can see it in the `Macro Editor` panel.
- Manually write codes in JavaScript  
  You can write macros in `Macro Editor` panel. `Macro Editor` panel will be shown by selecting the menu `Toggle Macro Tools`.


### Global Variables

You can use the following global variables.

- `App`  
  The instance of [App](http://127.0.0.1:4000/macro_api/com/smoothcsv/core/macro/api/App.html). This represents the application itself.
- `Clipboard`  
  The instance of [Clipboard](http://127.0.0.1:4000/macro_api/com/smoothcsv/core/macro/api/Clipboard.html).
- `Window`  
  The instance of [Window](http://127.0.0.1:4000/macro_api/com/smoothcsv/core/macro/api/Window.html).
- `CsvProperties`  
  A class that represents CSV Properties.
- `Macro`  
  A class which encapsulates macro script. You can call another macro from a main macro through this class.
- `console`  
  A object which is very similar to browsers' console API. `console` has a method `log` that outputs its arguments to SmoothCSV's console.

Global scope itself can be accessed by the name `global`.

### Macro API

<a href="/macro_api/" target="_blank">Open Macro API (Javadoc)</a>

Note: SmoothCSV's macro APIs are implemented in Java, though macro scripts themselves are written in JavaScript.
