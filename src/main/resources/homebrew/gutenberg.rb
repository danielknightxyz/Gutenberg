
class Gutenberg < Formula
    desc "A template and scaffolding utility."
    homepage "https://github.com/sourcefoundryus/gutenberg"
    url "https://github.com/sourcefoundryus/gutenberg/releases/download/${version}/gutenberg-${version}.zip"
    sha256 "${sha256}"
    version "${version}"

    bottle :unneeded

    def install
        lib.install "gutenberg-${version}.jar"
        bin.install "gutenberg"
    end
end