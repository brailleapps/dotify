# Introduction #

In addition to the Dotify project, there is a DotifyDevtools project in the code repository. DotifyDevtools contains tools that are useful when developing for Dotify, but are not needed when running the final software.

# Tools #
Tools included in devtools:
  * GenerateTableEntries
  * CodePointUtility
  * SchematronRulesGenerator
  * PefFileCompareUI
  * Unbrailler
  * and more

## GenerateTableEntries ##
This tool allows a user to generate braille table entries for a span of unicode characters that are to be added to a braille table. Currently, the parameters are embedded in the code, and need to be recompiled if modified.

## CodePointUtility ##
This tool is a Swing UI that allows a user to get unicode code points for a string of characters or the other way around. In addition, it allows a user to convert braille p-notation into the corresponding unicode braille pattern (block 0x2800).

## SchematronRulesGenerator ##
This tool can be used to build schematron files using a compact java notation.

## PefFileCompareUI ##
This tool can be used for regression testing. It allows detailed comparing between two folders of pef-files, ignoring meta data.

## Unbrailler ##
This tool can be used to compare text differences in an xml-editor. A folder is scanned for PEF-files and the braille in each file is replaced by ascii characters for easier debugging.