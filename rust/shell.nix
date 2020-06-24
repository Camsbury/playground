let
  pkgs = import <nixpkgs> {};
in
with pkgs;
mkShell {
  buildInputs = [
    cargo
    rustc
    rustracer
    rustfmt
  ];
}
