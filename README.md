# SiteSnap
Website Snapshot Webapp

This project provides a website from where site visitors are able to generate
snapshots of any given URL.

There is also an API end-point that can be invoked to generate a snapshot and
have the binary image returned directly.

The web interface image will be encoded per the standard JPG at 60%, whereas
the API will generate a lossless PNG.
