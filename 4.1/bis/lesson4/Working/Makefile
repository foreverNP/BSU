build:
	ml64 /Fl SampleNEw.txt /link /subsystem:windows /defaultlib:kernel32.lib /defaultlib:user32.lib /entry:Start

run: build
	./SampleNEw.exe

ddos:
	ml64 /Fl ddos.txt /link /subsystem:windows /defaultlib:kernel32.lib /defaultlib:user32.lib /entry:Start
	./ddos.exe