name=argus-pep-common
spec=fedora/$(name).spec
version=$(shell grep "Version:" $(spec) | sed -e "s/Version://g" -e "s/[ \t]*//g")
release=1

rpmbuild_dir=$(CURDIR)/rpmbuild
debbuild_dir = $(CURDIR)/debbuild
maven_nexus_settings_file=project/emi-maven-settings.xml
maven_settings_file=project/maven-settings.xml
stage_dir=$(CURDIR)/stage
prefix=/

.PHONY: etics package clean rpm

all: package rpm

clean:
	rm -rf target $(rpmbuild_dir) stage tgz RPMS $(spec)

spec:
	sed -e 's#@@BUILD_SETTINGS@@#-s $(maven_settings_file)#g' $(spec).in > $(spec)

package: spec
	mvn -B -s $(maven_settings_file) package

spec-etics:
	sed -e 's#@@BUILD_SETTINGS@@#-s $(maven_nexus_settings_file)#g' $(spec).in > $(spec)

package-etics: spec-etics
	mvn -B -s $(maven_nexus_settings_file) package


rpm: package
	@echo "Building RPM and SRPM"
	mkdir -p $(rpmbuild_dir)/BUILD $(rpmbuild_dir)/RPMS $(rpmbuild_dir)/SOURCES $(rpmbuild_dir)/SPECS $(rpmbuild_dir)/SRPMS
	cp target/$(name)-$(version).src.tar.gz $(rpmbuild_dir)/SOURCES/$(name)-$(version).tar.gz
	rpmbuild --nodeps -v -ba $(spec) --define "_topdir $(rpmbuild_dir)"


debian: package
	debuild --version
	@echo "Building Debian package in $(debbuild_dir)"
	mkdir -p $(debbuild_dir)
	cp target/$(name)-$(version).src.tar.gz $(debbuild_dir)/$(name)_$(version).orig.tar.gz
	tar -C $(debbuild_dir) -xf target/$(name)-$(version).src.tar.gz
	mv $(debbuild_dir)/$(name) $(debbuild_dir)/$(name)-$(version) 
	cd $(debbuild_dir)/$(name)-$(version) && debuild -us -uc 


etics:
	@echo "Publish RPMS, SRPMS and tarball"
	test -f $(rpmbuild_dir)/SRPMS/$(name)-$(version)-*.src.rpm
	mkdir -p tgz RPMS
	cp target/*.tar.gz tgz
	cp -r $(rpmbuild_dir)/RPMS/* $(rpmbuild_dir)/SRPMS/* RPMS

install:
	@echo "Install binary in $(DESTDIR)$(prefix)"
	test -f target/$(name)-$(version).tar.gz
	mkdir -p $(DESTDIR)$(prefix)
	tar -C $(DESTDIR)$(prefix) -xvzf target/$(name)-$(version).tar.gz

stage:
	echo "Staging tarball in $(stage_dir)"
	mkdir -p $(stage_dir)
	tar -C $(stage_dir) -xvzf target/$(name)-$(version).tar.gz

