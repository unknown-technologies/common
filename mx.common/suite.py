suite = {
  "mxversion" : "5.175.4",
  "name" : "common",
  "versionConflictResolution" : "latest",

  "javac.lint.overrides" : "none",

  "licenses" : {
    "GPLv3" : {
      "name" : "GNU General Public License, version 3",
      "url" : "https://opensource.org/licenses/GPL-3.0",
    }
  },

  "defaultLicense" : "GPLv3",

  "projects" : {
    "com.unknown.util" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.db" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util",
        "com.unknown.xml"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.math" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.xml" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.posix" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.vm" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.posix",
        "com.unknown.math"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.vm.power" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.vm"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.syntax" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.audio" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.text" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.plaf.motif" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util"
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.util.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.util",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.db.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.db",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.math.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.math",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.xml.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.xml",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.posix.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.posix",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.vm.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.vm",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.vm.power.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.vm.power",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.syntax.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.syntax",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.audio.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.audio",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    },

    "com.unknown.text.test" : {
      "subDir" : "projects",
      "sourceDirs" : ["src"],
      "dependencies" : [
        "com.unknown.text",
        "mx:JUNIT",
      ],
      "javaCompliance" : "21+",
      "workingSets" : "common",
      "license" : "GPLv3",
    }
  },

  "distributions" : {
    "CORE" : {
      "path" : "build/core.jar",
      "subDir" : "common",
      "sourcesPath" : "build/core.src.zip",
      "dependencies" : [
        "com.unknown.util",
        "com.unknown.math",
        "com.unknown.xml"
      ]
    },

    "AUDIO" : {
      "path" : "build/audio.jar",
      "subDir" : "common",
      "sourcesPath" : "build/audio.src.zip",
      "dependencies" : [
        "com.unknown.audio"
      ],
      "distDependencies" : [
        "CORE"
      ]
    },

    "DB" : {
      "path" : "build/db.jar",
      "subDir" : "common",
      "sourcesPath" : "build/db.src.zip",
      "dependencies" : [
        "com.unknown.db"
      ],
      "distDependencies" : [
        "CORE"
      ]
    },

    "MOTIF" : {
      "path" : "build/motif.jar",
      "subDir" : "common",
      "sourcesPath" : "build/motif.src.zip",
      "dependencies" : [
        "com.unknown.plaf.motif"
      ],
      "distDependencies" : [
        "CORE"
      ]
    },

    "TEXT" : {
      "path" : "build/text.jar",
      "subDir" : "common",
      "sourcesPath" : "build/text.src.zip",
      "dependencies" : [
        "com.unknown.text"
      ],
      "distDependencies" : [
        "CORE"
      ]
    },

    "SYNTAX" : {
      "path" : "build/syntax.jar",
      "subDir" : "common",
      "sourcesPath" : "build/syntax.src.zip",
      "dependencies" : [
        "com.unknown.syntax"
      ],
      "distDependencies" : [
        "CORE"
      ]
    },

    "POSIX" : {
      "path" : "build/posix.jar",
      "subDir" : "common",
      "sourcesPath" : "build/posix.src.zip",
      "dependencies" : [
        "com.unknown.posix",
      ],
      "distDependencies" : [
        "CORE"
      ]
    },

    "POWER" : {
      "path" : "build/powervm.jar",
      "subDir" : "common",
      "sourcesPath" : "build/powervm.src.zip",
      "dependencies" : [
        "com.unknown.vm.power",
      ],
      "distDependencies" : [
        "CORE",
        "POSIX"
      ]
    },

    "CORE_TEST" : {
      "path" : "build/core_test.jar",
      "subDir" : "common",
      "sourcesPath" : "build/core_test.src.zip",
      "dependencies" : [
        "com.unknown.util.test",
        "com.unknown.db.test",
        "com.unknown.math.test",
        "com.unknown.xml.test",
        "com.unknown.syntax.test",
        "com.unknown.audio.test"
      ],
      "exclude" : [
        "mx:JUNIT"
      ],
      "distDependencies" : [
        "common:CORE",
        "common:AUDIO",
        "common:DB",
        "common:TEXT",
        "common:SYNTAX"
      ]
    },

    "POSIX_TEST" : {
      "path" : "build/posix_test.jar",
      "subDir" : "common",
      "sourcesPath" : "build/posix_test.src.zip",
      "dependencies" : [
        "com.unknown.posix.test"
      ],
      "exclude" : [
        "mx:JUNIT"
      ],
      "distDependencies" : [
        "common:CORE",
        "common:POSIX"
      ]
    },

    "POWER_TEST" : {
      "path" : "build/powervm_test.jar",
      "subDir" : "common",
      "sourcesPath" : "build/powervm_test.src.zip",
      "dependencies" : [
        "com.unknown.vm.test",
        "com.unknown.vm.power.test"
      ],
      "exclude" : [
        "mx:JUNIT"
      ],
      "distDependencies" : [
        "common:CORE",
        "common:POWER"
      ]
    },

    "OSCILLOSCOPE" : {
      "path" : "build/oscilloscope.jar",
      "subDir" : "common",
      "sourcesPath" : "build/oscilloscope.src.zip",
      "mainClass" : "com.unknown.audio.AudioInOscilloscope",
      "strip" : [ "oscilloscope" ],
      "dependencies" : [
        "com.unknown.audio"
      ],
      "overlaps" : [
        "CORE",
        "AUDIO",
        "AUTOSAMPLE",
        "S550"
      ]
    },

    "AUTOSAMPLE" : {
      "path" : "build/autoloop.jar",
      "subDir" : "common",
      "sourcesPath" : "build/autoloop.src.zip",
      "mainClass" : "com.unknown.audio.AutoSample",
      "strip" : [ "autoloop" ],
      "dependencies" : [
        "com.unknown.audio"
      ],
      "overlaps" : [
        "CORE",
        "AUDIO",
        "OSCILLOSCOPE",
        "S550"
      ]
    },

    "S550" : {
      "path" : "build/s550.jar",
      "subDir" : "common",
      "sourcesPath" : "build/s550.src.zip",
      "mainClass" : "com.unknown.audio.s550.ui.MainWindow",
      "strip" : [ "oscilloscope" ],
      "dependencies" : [
        "com.unknown.audio"
      ],
      "overlaps" : [
        "CORE",
        "AUDIO",
        "OSCILLOSCOPE",
        "AUTOSAMPLE"
      ]
    }
  }
}
