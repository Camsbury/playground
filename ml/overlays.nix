self: super: {
  clj-kondo = super.callPackage (import ./kondo.nix) {};
  python3 = super.python3.override {
    packageOverrides = pythonSelf: pythonSuper: {
      urllib3 = pythonSuper.urllib3.overridePythonAttrs(oldAttrs: rec {
        version = "1.24.3";
        src = oldAttrs.src.override {
          inherit version;
          sha256 = "1x0slqrv6kixkbcdnxbglvjliwhc1payavxjvk8fvbqjrnasd4r3";
        };
      });

      kaggle = pythonSuper.buildPythonPackage rec {
        pname = "kaggle";
        version = "1.5.6";
        src = pythonSuper.fetchPypi {
          inherit pname version;
          sha256 = "0f5qrkgklcpgbwncrif7aw4f86dychqplh7k3f4rljwnr9yhjb1w";
        };
        propagatedBuildInputs = with pythonSelf; [
          certifi
          python-dateutil
          python-slugify
          requests
          six
          tqdm
          urllib3
        ];
        doCheck = false;
      };
    };
  };
}
