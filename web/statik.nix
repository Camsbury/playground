{stdenv, pkgs}:

with pkgs;
stdenv.mkDerivation (rec {
  version = "ce540d1";
  pname = "statik";
  name = "${pname}-${version}";
  src = builtins.fetchTarball {
    url = "https://github.com/teknql/statik/archive/ce540d1.tar.gz";
  };
  buildInputs = [
    graalvm8
    clojure
  ];
  installPhase = ''
    mkdir -p $out
    clj -Anative-image
    cp -r ${src}/* $out
  '';
  phases = [
    "unpackPhase"
    "installPhase"
  ];
})
