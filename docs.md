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
There are basically 2 ways to create macros.

- Record keys
- Manually write it in JavaScript

### Global Variables

You can use the following global variables.

- `App`  
  The instance of [App](http://127.0.0.1:4000/macro_api/com/smoothcsv/core/macro/api/App.html). This represents the application itself.
- `Clipboard`  
  The instance of [Clipboard](http://127.0.0.1:4000/macro_api/com/smoothcsv/core/macro/api/Clipboard.html).
- `console`  
  The instance of [Console](http://127.0.0.1:4000/macro_api/com/smoothcsv/core/macro/api/Console.html).
- `CsvProperties`  
  A class that represents CSV Properties.
- `Macro`  
  A class which encapsulates macro script. You can call another macro from a main macro through this class.

Global scope itself can be accessed by the name `global`.

### Macro API

<a href="/macro_api/" target="_blank">Open Macro API (Javadoc)</a>