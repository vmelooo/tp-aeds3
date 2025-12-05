{
  description = "JavaFX Project Environment and Launcher";

  inputs = {
    nixpkgs.url = "github:NixOS/nixpkgs/nixpkgs-unstable";
    utils.url = "github:numtide/flake-utils";
  };

  outputs =
    {
      self,
      nixpkgs,
      utils,
      ...
    }:
    let
      javaVersion = 21;
    in
    {
      overlays.default =
        final: prev:
        let
          jdk = prev."jdk${toString javaVersion}";
        in
        {
          inherit jdk;
          maven = prev.maven.override { jdk_headless = jdk; };
          lombok = prev.lombok.override { inherit jdk; };
        };
    }
    // utils.lib.eachDefaultSystem (
      system:
      let
        pkgs = import nixpkgs {
          inherit system;
          overlays = [ self.overlays.default ];
        };

        # required for JavaFX
        nativeLibs = with pkgs; [
          zlib
          libGL
          gtk3
          glib
          xorg.libXxf86vm
          xorg.libXtst
        ];

        # LD_LIBRARY_PATH string
        libraryPath = pkgs.lib.makeLibraryPath nativeLibs;

        # sets the environment variables and runs Maven
        launcherScript = pkgs.writeShellScriptBin "tp-aeds3" ''
          export JAVA_TOOL_OPTIONS="-javaagent:${pkgs.lombok}/share/java/lombok.jar"
          export LD_LIBRARY_PATH="${libraryPath}:$LD_LIBRARY_PATH"

          echo "Launching TP-AEDS3..."
          # Executes maven in the current directory
          ${pkgs.maven}/bin/mvn clean javafx:run
        '';

      in
      {
        # --- `nix build` ---
        packages.default = launcherScript;

        # --- `nix run` ---
        apps.default = utils.lib.mkApp {
          drv = launcherScript;
        };

        # --- `nix develop` ---
        devShells.default = pkgs.mkShell rec {
          packages = [
            pkgs.jdk
            pkgs.maven
            pkgs.lombok
          ]
          ++ nativeLibs;

          shellHook =
            let
              loadLombok = "-javaagent:${pkgs.lombok}/share/java/lombok.jar";
              prev = "\${JAVA_TOOL_OPTIONS:+ $JAVA_TOOL_OPTIONS}";
            in
            ''
              export JAVA_TOOL_OPTIONS="${loadLombok}${prev}"
              export LD_LIBRARY_PATH="${libraryPath}:$LD_LIBRARY_PATH"
              echo "☕ Java Development Environment Loaded (Java ${toString javaVersion})"
            '';
        };
      }
    );
}
