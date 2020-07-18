let
  pkgs = import <nixpkgs> {
    overlays = [(import ./overlays.nix)];
  };
in
  with pkgs;
  mkShell {
    name = "web-shell";
    buildInputs = [
      clojure
      openjdk
      clj-kondo
      statik
    ];
    shellHook = "export STATIK=${statik}";
  }
