name=argus-pep-common
spec=fedora/$(name).spec
version=$(shell grep "Version:" $(spec) | sed -e "s/Version://g" -e "s/[ \t]*//g")
release=1

rpmbuild_dir=$(CURDIR)/rpmbuild
debbuild_dir = $(CURDIR)/debbuild
#maven_settings_file=project/emi-maven-settings.xml
maven_settings_file=project/maven-settings.xml
stage_dir=$(CURDIR)/stage
tmp_dir=$(CURDIR)/tmp
prefix=/

.PHONY: etics package clean rpm

all: package

clean:
	rm -rf target $(rpmbuild_dir) $(debbuild_dir) $(tmp_dir) *.tar.gz stage tgz RPMS $(spec)

spec:
	sed -e 's#@@BUILD_SETTINGS@@#-s $(maven_settings_file)#g' $(spec).in > $(spec)

package: spec
	mvn -B -s $(maven_settings_file) package

dist: package
	@echo "Repackaging the maven source tarball..."
	test ! -d $(tmp_dir) || rm -fr $(tmp_dir)
	mkdir -p $(tmp_dir)
	tar -C $(tmp_dir) -xzf target/$(name)-$(version).src.tar.gz
	mv $(tmp_dir)/$(name) $(tmp_dir)/$(name)-$(version)
	test ! -f $(name)-$(version).tar.gz || rm $(name)-$(version).tar.gz
	tar -C $(tmp_dir) -czf $(name)-$(version).tar.gz $(name)-$(version)
	rm -fr $(tmp_dir)


rpm: dist
	@echo "Building RPM and SRPM"
	mv $(name)-$(version).tar.gz $(name)-$(version).src.tar.gz
	mkdir -p $(rpmbuild_dir)/BUILD $(rpmbuild_dir)/RPMS $(rpmbuild_dir)/SOURCES $(rpmbuild_dir)/SPECS $(rpmbuild_dir)/SRPMS
	cp $(name)-$(version).src.tar.gz $(rpmbuild_dir)/SOURCES/$(name)-$(version).tar.gz
	rpmbuild --nodeps -v -ba $(spec) --define "_topdir $(rpmbuild_dir)"


debian: dist
	@echo "Building Debian package in $(debbuild_dir)"
	mv $(name)-$(version).tar.gz $(name)-$(version).src.tar.gz
	mkdir -p $(debbuild_dir)
	cp $(name)-$(version).src.tar.gz $(debbuild_dir)/$(name)_$(version).orig.tar.gz
	tar -C $(debbuild_dir) -xzf $(name)-$(version).src.tar.gz
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
