let
  pkgs = import <nixpkgs> {
    overlays = [(import ./overlays.nix)];
  };
  python-custom = (pkgs.python3.withPackages (
        pythonPackages: with pythonPackages; [
          matplotlib
          numpy
          pandas
          pillow
          scikitlearn
          scipy
          statsmodels
        ]));
in
  with pkgs;
  mkShell {
    name = "portfoloShell";
    buildInputs = [
      clojure
      clj-kondo
      python-custom
    ];
    shellHooks = ''
      export PYTHON_PATH=${python-custom}
      export PYTHON_VERSION=${python3.version}
    '';
  }
