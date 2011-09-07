require 'rake'
require 'mirah'

libs = ['lib/swt.jar',
        'lib/swt-gtk-linux-x86-3.5.2.jar',
        'lib/commons-configuration-1.6.jar',
        'lib/commons-lang-2.4.jar']

src = 'src/mirah'

classpath = Mirah::Env.encode_paths(libs)

task :compile do
  Mirah.compile('-c', classpath, '--cd', src, '-d', '../../classes', '.')
end

task :java do
  Mirah.compile('-j', '-c', classpath, '--cd', src, '-d', '../java', '.')
end
