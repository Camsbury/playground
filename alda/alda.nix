{stdenv, fetchurl, pkgs}:

stdenv.mkDerivation (rec {
  # version = "1.3.3";
  version = "1.4.2";
  pname = "alda";
  name = "${pname}-${version}";
  src = fetchurl {
    url = https://github.com/alda-lang/alda/releases/download + "/${version}/alda";
    # sha256 = "1jv3ji96h3wral7rvimc39sfr9f9vnkmmh51babc2cjc786ibdl7";
    sha256 = "1d0412jw37gh1y7i8cmaml8r4sn516i6pxmm8m16yprqmz6glx28";
  };
  propagatedBuildInputs = [
    pkgs.openjdk
  ];
  installPhase = ''
    mkdir -p $out/bin
    cp ${src} $out/bin/alda
    chmod +x $out/bin/alda
  '';
  dontUnpack = true;
})
