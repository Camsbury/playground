{ nixpks ? import <nixpkgs> {}}:
let
  inherit (nixpkgs) pkgs;
  inherit (pkgs) haskellPackages;
  project = (import ./default.nix { inherit nixpkgs; });
in
pkgs.stdenv.mkderivation {
  name = "haskell-playground-shell";
  buildInputs = project.env.nativeBuildInputs ++ [
    haskellPackages.cabal-install
  ];
}
