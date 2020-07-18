self: super: {
  clj-kondo = super.callPackage (import ./kondo.nix) {};
  graalvm8 = super.graalvm8.overrideAttrs (oldAttrs: rec {
    postInstall = ''
      $out/bin/gu install native-image
    '';
  });
  statik = super.callPackage (import ./statik.nix) {};
}
