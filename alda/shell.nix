let
  pkgs = import <nixpkgs> {
    overlays = [(import ./overlays.nix)];
  };
in
  with pkgs;
  mkShell {
    name = "alda-shell";
    buildInputs = [
      alda
    ];
  }
