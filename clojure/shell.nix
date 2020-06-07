let
  pkgs = import <nixpkgs> {
    overlays = [(import ./overlays.nix)];
  };
in
  with pkgs;
  mkShell {
    name = "clojureShell";
    buildInputs = [
      clojure
      clj-kondo
      graphviz
      leiningen
      openjdk
    ];
  }
