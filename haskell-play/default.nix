{ nixpks ? import <nixpkgs> {}}:
nixpkgs.pkgs.haskellPackages.callCabal2nix "haskell-play" ./. {}
