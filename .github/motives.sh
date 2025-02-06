#!/bin/bash

# The MIT License (MIT)
#
# Copyright (c) 2016-2025 Objectionary.com
#
# Permission is hereby granted, free of charge, to any person obtaining a copy
# of this software and associated documentation files (the "Software"), to deal
# in the Software without restriction, including without limitation the rights
# to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
# copies of the Software, and to permit persons to whom the Software is
# furnished to do so, subject to the following conditions:
#
# The above copyright notice and this permission notice shall be included
# in all copies or substantial portions of the Software.
#
# THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
# IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
# FITNESS FOR A PARTICULAR PURPOSE AND NON-INFRINGEMENT. IN NO EVENT SHALL THE
# AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
# LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
# OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
# SOFTWARE.

set -e

tag=$1
if [ -z "${tag}" ]; then
  echo "Provide tag name as the first argument, for example '0.0.1'"
  exit 1
fi

rm -rf "gh-pages/${tag}"
mkdir -p "gh-pages/${tag}"

while IFS= read -r f; do
  n=$(basename "${f}" .md)
  html=gh-pages/${tag}/${n}.html
  pandoc "${f}" -o "${html}"
  echo "${n} -> $(du -b "${html}" | cut -f1) bytes"
done < <(find src/main/resources/org/eolang/motives -name '*.md')

function list_them() {
  printf "<ul>\n"
  while IFS= read -r f; do
    n=$(basename "${f}" ".html")
    printf '<li><a href="%s">%s</a></li>\n' \
      "/lints/${tag}/${n}.html" "${n}"
  done < <(find "gh-pages/${tag}" -name '*.html' | sort)
  printf '</ul>\n'
}

function head() {
  printf '<html><body style="font-family: monospace;">\n'
}

function tail() {
  printf '<p>Published on %s.</p>\n' "$(date)"
  printf '</body></html>'
}

(
  head
  printf '<p>Latest version: <a href="/lints/%s/index.html">%s</a></p>\n' "${tag}" "${tag}"
  printf '<p>List of lints in this version:<p>'
  list_them
  tail
) > "gh-pages/index.html"

(
  head
  printf "<p>List of lints in %s:<p>" "${tag}"
  list_them
  tail
) > "gh-pages/${tag}/index.html"
