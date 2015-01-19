/*
 * (C) Copyright 2014 Mariusz Brylant (mbrylant@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.sample;

public class ProcessMain {

	public static void main(String[] args) {
//		KieServices ks = KieServices.Factory.get();
//		KieContainer kContainer = ks.getKieClasspathContainer();
//		KieBase kbase = kContainer.getKieBase("kbase");
//
//		RuntimeManager manager = createRuntimeManager(kbase);
//		RuntimeEngine engine = manager.getRuntimeEngine(null);
//		KieSession ksession = engine.getKieSession();
//		TaskService taskService = engine.getTaskService();
//
//		ksession.startProcess("com.sample.bpmn.hello");
//
//		// let john execute Task 1
//		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner("john", "en-UK");
//		TaskSummary task = list.get(0);
//		System.out.println("John is executing task " + task.getName());
//		taskService.start(task.getId(), "john");
//		taskService.complete(task.getId(), "john", null);
//
//		// let mary execute Task 2
//		list = taskService.getTasksAssignedAsPotentialOwner("mary", "en-UK");
//		task = list.get(0);
//		System.out.println("Mary is executing task " + task.getName());
//		taskService.start(task.getId(), "mary");
//		taskService.complete(task.getId(), "mary", null);
//
//		manager.disposeRuntimeEngine(engine);
//		System.exit(0);
	}

//	private static RuntimeManager createRuntimeManager(KieBase kbase) {
//		JBPMHelper.startH2Server();
//		JBPMHelper.setupDataSource();
//		EntityManagerFactory emf = Persistence.createEntityManagerFactory("org.jbpm.persistence.jpa");
//		RuntimeEnvironmentBuilder builder = RuntimeEnvironmentBuilder.Factory.get()
//			.newDefaultBuilder().entityManagerFactory(emf)
//			.knowledgeBase(kbase);
//		return RuntimeManagerFactory.Factory.get()
//			.newSingletonRuntimeManager(builder.get(), "com.sample:example:1.0");
//	}

}