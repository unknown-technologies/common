common
======

This repository contains useful classes and subroutines for various Java
projects.


Features
--------

- RIFF WAVE file library with advanced chunks like smpl/inst
- MIDI file library with full access to all low level information
- RS232 decoder from noisy raw waveform
- MIDI Sysex classes for certain sound modules (KORG MS2000, ...)
- Sample parsers for certain samplers (Roland S-550, E-MU Emulator I, ...)
- Nintendo DSPADPCM encoder/decoder
- Audio analysis routines for sampled audio
- Building blocks for a basic synthesizer in Java (voice allocator, ...)
- u-law encoding/decoding with multiple different standards
- Fancy oscilloscope / waveform view with cursors and text overlay
- Simple logging mechanism using java.util.logging
- Database classes to easily and elegantly write DAO classes
- Hyphenation algorithm
- Syntax highlighter
- Math routines including matrix/vector math for 3D graphics
- Common routines for endianess conversion and binary stream I/O
- Base64 and Base85 encoder/decoder
- Push based XML parser + serializer with SAX/DOM-like API
- JSON parser + serializer
- XPM image parser
- TAR archive parser
- TELNET implementation including TN3270
- Art-Net implementation to control DMX lights
- ShowNET implementation to use ILDA streaming with Laserworld show lasers
- minimal RRD-like timeseries database
- ELF file parser
- Pure Java based POSIX/Linux compatible API emulation including VFS
- Pure Java implementation of Virtual Memory for use in emulators
- Power8 emulator with Linux userspace ABI (can run various Linux programs)
- Swing MOTIF L&F since it was removed from upstream JDK


Building
--------

- Download a suitable JDK (JDK21+ at the moment)
- Install [mx](https://github.com/graalvm/mx)
- Create the file `mx.common/env` with a line like
  `JAVA_HOME=/usr/lib/jvm/java-21-openjdk` (set the JDK path
  appropriately)
- `mx build`
- To run the JUnit tests: `mx unit com.unknown.`

The compiled library JAR files are generated in the `build` folder.


Development
-----------

You can easily import the code into your IDE of choice. To do this, set
up the build environment according to the instructions in the "Building"
section. Then run `mx eclipseinit` or `mx ideinit` to generate IDE
project files. Once the project files are generated, import all project
files from the root of the repository + subfolders into your IDE.

This will not only set up classpaths and JDK compatibility levels, it
will _also_ set up code formatting rules as well as various rules
related to compiler warnings.


License
-------

This library is licensed under the terms of the GNU GPLv3. You want to use
it in your commercial closed source project? Bad luck, look elsewhere.
