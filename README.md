visitor-maven-plugin
====================

Visitor pattern boilerplate generator

``` xml
<project>
    <build>
        <plugins>
            <plugin>
                <groupId>com.yahoo.maven.visitor</groupId>
                <artifactId>visitor-maven-plugin</artifactId>
                <version>0.0.13</version>
                <executions>
                    <execution>
                        <id>generate-visitors</id>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate-visitors</goal>
                        </goals>
                        <configuration>
                            <visitors>
                                <visitor>
                                    <visitedName>com.yahoo.maven.visitor.test.Expression</visitedName>
                                    <typeParameters>
                                        <typeParameter>
                                            <name>Other</name>
                                        </typeParameter>
                                    </typeParameters>
                                    <disjunctions>
                                        <disjunct>
                                            <name>literalString</name>
                                            <parameters>
                                                <parameter>
                                                    <type>java.lang.String</type>
                                                    <name>string</name>
                                                </parameter>
                                            </parameters>
                                        </disjunct>
                                        <disjunct>
                                            <name>concatenation</name>
                                            <parameters>
                                                <parameter>
                                                    <type>Expression&lt;Other&gt;</type>
                                                    <name>left</name>
                                                </parameter>
                                                <parameter>
                                                    <type>Expression&lt;Other&gt;</type>
                                                    <name>right</name>
                                                </parameter>
                                            </parameters>
                                        </disjunct>
                                        <disjunct>
                                            <name>other</name>
                                            <parameters>
                                                <parameter>
                                                    <type>Other</type>
                                                    <name>other</name>
                                                </parameter>
                                            </parameters>
                                        </disjunct>
                                    </disjunctions>
                                </visitor>
                            </visitors>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    <repositories>
        <repository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-sciolizer-maven</id>
            <name>bintray</name>
            <url>http://dl.bintray.com/sciolizer/maven</url>
        </repository>
    </repositories>
    <pluginRepositories>
        <pluginRepository>
            <snapshots>
                <enabled>false</enabled>
            </snapshots>
            <id>bintray-sciolizer-maven</id>
            <name>bintray-plugins</name>
            <url>http://dl.bintray.com/sciolizer/maven</url>
        </pluginRepository>
    </pluginRepositories>
</project>
```

``` bash
mvn versions:display-plugin-updates
mvn clean generate-sources
```

This will generate interfaces such as the following

``` java
public interface Expression<Other> {

    <E extends Exception, T> T accept(ExpressionVisitor<? super Other, E, T> visitor) throws E;

}

public interface ExpressionVisitor<Other, E extends Exception, T> {

    T literalString(java.lang.String string) throws E;

    T concatenation(Expression<? extends Other> left, Expression<? extends Other> right) throws E;

    T other(Other other) throws E;

}
```

along with a few convenience visitors and factories. Instances created with the factories will have compliant
implementations of `equals`, `hashCode`, and `toString`.

Code licensed under the Apache 2.0 license. See LICENSE file for terms.
