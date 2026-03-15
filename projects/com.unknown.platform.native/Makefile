.PHONY: all

OBJECTS		=	serial.o

ifeq ($(OS),windows)
OBJECTS		+=	registry.o
OSINC		:=	win32
CFLAGS		+=	-O3 -g
else
OSINC		:=	$(OS)
CFLAGS		+=	-O3 -g -fPIC
endif

OBJECTFILES	=	${OBJECTS:%.o=bin/%.o}
CFLAGS		+=	-I$(VPATH)/include -Isrc_gen "-I$(JAVA_HOME)/include" "-I$(JAVA_HOME)/include/$(OSINC)"
LDFLAGS		=	-g

all: bin/$(LIBSERIAL)

bin:
	@mkdir $@

bin/$(LIBSERIAL): $(OBJECTFILES)
	@echo "[LD]        $@"
	@$(CC) -std=c99 -shared $(LDFLAGS) $+ -o $@

ifneq ($(OS),windows)
bin/%.d: src/%.c
	@mkdir -p $(@D)
	@$(CC) -MM $(CFLAGS) $< | sed 's|\($*\)\.o[ :]*|bin/\1.o $@ : |g' > $@
endif

bin/%.o: src/%.c | bin
	@echo "[CC]        $@"
	@$(CC) -c $(CFLAGS) -o $@ $<

ifneq ($(OS),windows)
-include ${OBJECTS:%.o=bin/%.d}
endif
