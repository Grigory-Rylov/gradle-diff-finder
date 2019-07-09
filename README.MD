**Tool for finding changed modules or modules with changed dependencies.**
Use it to for TIA (Test impact analysis)
No need to run tests in those modules where there were no changes.

[ ![Download](https://api.bintray.com/packages/grigory-rylov/gradle/gradle-diff-finder/images/download.svg?version=0.1.0) ](https://bintray.com/grigory-rylov/gradle/gradle-diff-finder/0.1.0/link)
1) create instance of `AffectedModules(targetBranch, pathToGit)`
    `targetBranch` - target branch for example master.
    `pathToGit` - path to git.
    
2) call `execute(project)`
    `project` - gradle module
    
result will be contains set of affected modules.

gradle:
```
compile 'com.github.grishberg:gradle-diff-finder:0.1.0'
```
maven:
```
<dependency>
	<groupId>com.github.grishberg</groupId>
	<artifactId>gradle-diff-finder</artifactId>
	<version>0.1.0</version>
	<type>pom</type>
</dependency>
```    
