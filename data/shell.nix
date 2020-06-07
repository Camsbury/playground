let
  pkgs = import <nixpkgs> {
    overlays = [(import ./overlays.nix)];
  };
in
  with pkgs;
  let
    myPython =
      python3.withPackages (
        pythonPackages: with pythonPackages; [
          ipython
          isort
          jedi
          jupyter
          jupyter_client
          jupyter_core
          kaggle
          matplotlib
          mypy
          numpy
          pandas
          pyflakes
          pylint
          scikitlearn
          seaborn
          yapf
        ]
      );
  in
    mkShell {
      name = "dataAnalysis";
      buildInputs = [
        myPython
      ];
    }
