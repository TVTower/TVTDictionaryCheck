# TVTDictionaryCheck
Tools for checking the TVTower dictionary files for consistency

## ThemeFoldersConsistencyChecker

Checks that all dictionary files in a theme folder have the same structure.
Even if there is no translation, the key must exist (as comment).

The code for checking the refactored dictionaries against the original dictionaries (one file per language) is still present but not in use anymore.

## KeyUsageChecker

Naive text search over the TVTower source code to identify keys that are not used in the source code anymore.
Reported keys can then be checked manually.

## Calculated keys

Some keys do not appear in the source code as such but are assembled typically from a fixed prefix and further strings (e.g. enum values).
`src/main/resources/prefixes.txt` contains some such prefixes.
Keys starting with such a prefix will not be reported as not being present in the source code.
Obviously this does not mean that the key will actually be used.

# TypeUsageChecker

Prototype for quick and dirty analysis of usages in bmx code