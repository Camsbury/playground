let
  pkgs = import <nixpkgs> {};
in
with pkgs;
mkShell {
  buildInputs = [
    astyle
    gcc
    gdb
    valgrind
  ];
}
