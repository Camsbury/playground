let
  pkgs = import <nixpkgs> {};
in
with pkgs;
mkShell {
  name = "idris-shell";
  buildInputs = [
    idris
  ];
}
