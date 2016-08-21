package com.github.javaparser.ast.nodeTypes;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.ASTHelper;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.FieldDeclaration;
import com.github.javaparser.ast.body.InitializerDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.body.VariableDeclaratorId;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;

/**
 * A node having members.
 * 
 * The main reason for this interface is to permit users to manipulate homogeneously all nodes with a getMembers
 * method.
 * 
 * 
 */
public interface NodeWithMembers<T> {
    List<BodyDeclaration<?>> getMembers();

    T setMembers(List<BodyDeclaration<?>> members);

    /**
     * Add a field to this and automatically add the import of the type if needed
     * 
     * @param typeClass the type of the field
     * @param name the name of the field
     * @param modifiers the modifiers like {@link Modifier#PUBLIC}
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addField(Class<?> typeClass, String name, Modifier... modifiers) {
        ((Node) this).tryAddImportToParentCompilationUnit(typeClass);
        return addField(typeClass.getSimpleName(), name, modifiers);
    }

    /**
     * Add a field to this
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @param modifiers the modifiers like {@link Modifier#PUBLIC}
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addField(String type, String name, Modifier... modifiers) {
        return addField(new ClassOrInterfaceType(type), name, modifiers);
    }

    /**
     * Add a field to this
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @param modifiers the modifiers like {@link Modifier#PUBLIC}
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addField(Type type, String name, Modifier... modifiers) {
        FieldDeclaration fieldDeclaration = new FieldDeclaration();
        fieldDeclaration.getVariables().add(new VariableDeclarator(new VariableDeclaratorId(name)));
        fieldDeclaration.setModifiers(Arrays.stream(modifiers)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Modifier.class))));
        fieldDeclaration.setType(type);
        getMembers().add(fieldDeclaration);
        fieldDeclaration.setParentNode((Node) this);
        return fieldDeclaration;
    }

    /**
     * Add a private field to this
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addPrivateField(Class<?> typeClass, String name) {
        return addField(typeClass, name, Modifier.PRIVATE);
    }

    /**
     * Add a private field to this and automatically add the import of the type if
     * needed
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addPrivateField(String type, String name) {
        return addField(type, name, Modifier.PRIVATE);
    }

    /**
     * Add a public field to this
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addPublicField(Class<?> typeClass, String name) {
        return addField(typeClass, name, Modifier.PUBLIC);
    }

    /**
     * Add a public field to this and automatically add the import of the type if
     * needed
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addPublicField(String type, String name) {
        return addField(type, name, Modifier.PUBLIC);
    }

    /**
     * Add a protected field to this
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addProtectedField(Class<?> typeClass, String name) {
        return addField(typeClass, name, Modifier.PROTECTED);
    }

    /**
     * Add a protected field to this and automatically add the import of the type
     * if needed
     * 
     * @param type the type of the field
     * @param name the name of the field
     * @return the {@link FieldDeclaration} created
     */
    public default FieldDeclaration addProtectedField(String type, String name) {
        return addField(type, name, Modifier.PROTECTED);
    }

    /**
     * Adds a methods with void return by default to this
     * 
     * @param methodName the method name
     * @param modifiers the modifiers like {@link Modifier#PUBLIC}
     * @return the {@link MethodDeclaration} created
     */
    public default MethodDeclaration addMethod(String methodName, Modifier... modifiers) {
        MethodDeclaration methodDeclaration = new MethodDeclaration();
        methodDeclaration.setName(methodName);
        methodDeclaration.setType(ASTHelper.VOID_TYPE);
        methodDeclaration.setModifiers(Arrays.stream(modifiers)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Modifier.class))));
        getMembers().add(methodDeclaration);
        methodDeclaration.setParentNode((Node) this);
        return methodDeclaration;
    }

    /**
     * Adds a constructor to this
     * 
     * @param methodName the method name
     * @param modifiers the modifiers like {@link Modifier#PUBLIC}
     * @return the {@link MethodDeclaration} created
     */
    public default ConstructorDeclaration addCtor(Modifier... modifiers) {
        ConstructorDeclaration constructorDeclaration = new ConstructorDeclaration();
        constructorDeclaration.setModifiers(Arrays.stream(modifiers)
                .collect(Collectors.toCollection(() -> EnumSet.noneOf(Modifier.class))));
        constructorDeclaration.setName(((TypeDeclaration<?>) this).getName());
        getMembers().add(constructorDeclaration);
        constructorDeclaration.setParentNode((Node) this);
        return constructorDeclaration;
    }

    public default BlockStmt addInitializer() {
        BlockStmt block = new BlockStmt();
        InitializerDeclaration initializerDeclaration = new InitializerDeclaration(false, block);
        getMembers().add(initializerDeclaration);
        initializerDeclaration.setParentNode((Node) this);
        return block;
    }

    public default BlockStmt addStaticInitializer() {
        BlockStmt block = new BlockStmt();
        InitializerDeclaration initializerDeclaration = new InitializerDeclaration(true, block);
        getMembers().add(initializerDeclaration);
        initializerDeclaration.setParentNode((Node) this);
        return block;
    }

    /**
     * Try to find a {@link MethodDeclaration} by its name
     * 
     * @param name the name of the method
     * @return the methods found (multiple in case of polymorphism)
     */
    public default List<MethodDeclaration> getMethodsByName(String name) {
        return getMembers().stream()
                .filter(m -> m instanceof MethodDeclaration && ((MethodDeclaration) m).getName().equals(name))
                .map(m -> (MethodDeclaration) m).collect(Collectors.toList());
    }

    /**
     * Try to find a {@link MethodDeclaration} by its parameters types
     * 
     * @param paramTypes the types of parameters like "Map&lt;Integer,String&gt;","int" to match<br>
     *            void foo(Map&lt;Integer,String&gt; myMap,int number)
     * @return the methods found (multiple in case of polymorphism)
     */
    public default List<MethodDeclaration> getMethodsByParameterTypes(String... paramTypes) {
        return getMembers().stream()
                .filter(m -> m instanceof MethodDeclaration
                        && ((MethodDeclaration) m).getParameters().stream().map(p -> p.getType().toString())
                                .collect(Collectors.toSet()).equals(Stream.of(paramTypes).collect(Collectors.toSet())))
                .map(m -> (MethodDeclaration) m).collect(Collectors.toList());
    }

    /**
     * Try to find a {@link MethodDeclaration} by its parameters types
     * 
     * @param paramTypes the types of parameters like "Map&lt;Integer,String&gt;","int" to match<br>
     *            void foo(Map&lt;Integer,String&gt; myMap,int number)
     * @return the methods found (multiple in case of polymorphism)
     */
    public default List<MethodDeclaration> getMethodsByParameterTypes(Class<?>... paramTypes) {
        return getMembers().stream()
                .filter(m -> m instanceof MethodDeclaration
                        && ((MethodDeclaration) m).getParameters().stream().map(p -> p.getType().toString())
                                .collect(Collectors.toSet())
                                .equals(Stream.of(paramTypes).map(c -> c.getSimpleName()).collect(Collectors.toSet())))
                .map(m -> (MethodDeclaration) m).collect(Collectors.toList());
    }

    /**
     * Try to find a {@link FieldDeclaration} by its name
     * 
     * @param name the name of the field
     * @return null if not found, the FieldDeclaration otherwise
     */
    public default FieldDeclaration getFieldByName(String name) {
        return (FieldDeclaration) getMembers().stream()
                .filter(m -> m instanceof FieldDeclaration && ((FieldDeclaration) m).getVariables().stream()
                        .anyMatch(var -> var.getId().getName().equals(name)))
                .findFirst().orElse(null);
    }
}