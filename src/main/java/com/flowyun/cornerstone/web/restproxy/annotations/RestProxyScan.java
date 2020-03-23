/**
 * Copyright 2010-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.flowyun.cornerstone.web.restproxy.annotations;

import com.flowyun.cornerstone.web.restproxy.scanner.RestProxyScannerRegistrar;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RestProxyScannerRegistrar.class)
public @interface RestProxyScan {

  String[] value() default {};

  /**
   * 扫描的包
   */
  String[] basePackages() default {};

  /**
   * 类型安全的方式制定扫描包
   */
  Class<?>[] basePackageClasses() default {};

  /**
   * 过滤接口
   **/
  Class<?> markerInterface() default Class.class;

  /**
   * The {@link BeanNameGenerator} class to be used for naming detected components within the Spring container.
   *
   * @return the class of {@link BeanNameGenerator}
   */
  Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;

  /*
  * 是否延迟初始化
  * */
  String lazyInitialization() default "";
}
