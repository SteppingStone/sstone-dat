
ICON_SRCDIR := $(HOME)/ws/edc/img/icons

LG_ICON_OUT := src/main/resources/icons/large
SM_ICON_OUT := src/main/resources/icons/small

SET_MOD_DIR := SSTONE_MODULE_DIR=/dev/shm/modules 

JAVA := java -Djava.ext.dirs=/home/greg/opt/jlayer/lib

MVN := mvn 

#
#	icon sources:
#		src/main/resources/icons/small/menu_screen.png (gnome icons)
#		src/main/resources/icons/small/delete.png	(eclipse icons)
#		src/main/resources/icons/small/play.png		(modified from tango)
#		src/main/resources/icons/small/down.png		(tango)
#		src/main/resources/icons/small/up.png		(tango)
#		src/main/resources/icons/small/audio_screen.png		(modified from gnome icons (frame) + tango)
#		src/main/resources/icons/small/screen_series.png	(gko)
#		src/main/resources/icons/small/edit.png			(tango: accessories-text-editor.png)
#		src/main/resources/icons/small/animated_screen.png	(modified from tango: gnome-multimedia.svg)
#		src/main/resources/icons/small/content_screen.png	(modified from gnome icons)
#		src/main/resources/icons/small/color_chooser.png	(from gorilla icon set)
#		src/main/resources/icons/small/folder.png		tango OOTB
#

all:
	$(MVN) -Dbundle.mp3.support=true package

#              -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel \

run:
	$(SET_MOD_DIR) $(JAVA) \
	    -Dswing.aatext=true \
	    -Dawt.useSystemAAFontSettings=lcd \
	    -jar target/sstone-dat-1.0.0.jar /dev/shm/modules/mod3.ssp

gtkdebug:
	$(SET_MOD_DIR) $(JAVA) \
	    -agentlib:jdwp=transport=dt_socket,address=9000,server=y,suspend=y \
	    -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel \
	    -Dswing.aatext=true \
	    -Dawt.useSystemAAFontSettings=lcd \
	    -jar target/sstone-dat-1.0.0.jar 

gtkrun:
	$(SET_MOD_DIR) $(JAVA) \
	    -Dswing.defaultlaf=com.sun.java.swing.plaf.gtk.GTKLookAndFeel \
	    -Dswing.aatext=true \
	    -Dawt.useSystemAAFontSettings=lcd \
	    -jar target/sstone-dat-1.0.0.jar 

nimbusrun:
	$(SET_MOD_DIR) $(JAVA) \
	    -Dswing.defaultlaf=com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel \
	    -Dswing.aatext=true \
	    -Dawt.useSystemAAFontSettings=lcd \
	    -jar target/sstone-dat-1.0.0.jar /dev/shm/modules/mod3.ssp
		
metalrun:
	$(SET_MOD_DIR) $(JAVA) \
	    -Dswing.defaultlaf=javax.swing.plaf.metal.MetalLookAndFeel \
	    -Dswing.aatext=true \
	    -Dawt.useSystemAAFontSettings=lcd \
	    -jar target/sstone-dat-1.0.0.jar 

push:
	mkdir -p /dev/shm/dat
	cp target/sstone-dat-1.0.0.jar /dev/shm/dat
	cp src/main/bin/windows/*cmd /dev/shm/dat

#	zip -X -j target/sstone-dat-1.0.0.zip target/sstone-dat-1.0.0.jar src/main/bin/windows/*cmd
dist:
	$(RM) target/sstone-dat-1.0.0.zip 
	zip -X -j /dev/shm/sstone-dat-`date +%Y%m%d`.zip target/sstone-dat-1.0.0.jar

icons:
	### Add a 2px alpha border
	convert -border 2 -bordercolor '#00000000' $(ICON_SRCDIR)/component_tree_20_noborder.png $(LG_ICON_OUT)/component_tree.png
	cp $(ICON_SRCDIR)/small/*png $(SM_ICON_OUT)

clean:
	mvn clean

.PHONY: all run
