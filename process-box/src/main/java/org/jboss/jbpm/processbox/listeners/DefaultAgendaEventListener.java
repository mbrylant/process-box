package org.jboss.jbpm.processbox.listeners;

import org.drools.WorkingMemory;
import org.drools.event.ActivationCancelledEvent;
import org.drools.event.ActivationCreatedEvent;
import org.drools.event.AfterActivationFiredEvent;
import org.drools.event.AgendaGroupPoppedEvent;
import org.drools.event.AgendaGroupPushedEvent;
import org.drools.event.BeforeActivationFiredEvent;
import org.drools.event.RuleFlowGroupActivatedEvent;
import org.drools.event.RuleFlowGroupDeactivatedEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultAgendaEventListener implements org.drools.event.AgendaEventListener, org.drools.event.rule.AgendaEventListener {
	
	private Logger log = LoggerFactory.getLogger(DefaultAgendaEventListener.class);

	public void activationCancelled(ActivationCancelledEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void activationCreated(ActivationCreatedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void afterActivationFired(AfterActivationFiredEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void afterRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void afterRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void agendaGroupPopped(AgendaGroupPoppedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void agendaGroupPushed(AgendaGroupPushedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void beforeActivationFired(BeforeActivationFiredEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void beforeRuleFlowGroupActivated(RuleFlowGroupActivatedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void beforeRuleFlowGroupDeactivated(RuleFlowGroupDeactivatedEvent arg0, WorkingMemory arg1) {
		log.debug(arg0.toString());
		System.out.println("x");
	}

	public void activationCancelled(
			org.drools.event.rule.ActivationCancelledEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void activationCreated(
			org.drools.event.rule.ActivationCreatedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void afterActivationFired(
			org.drools.event.rule.AfterActivationFiredEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void afterRuleFlowGroupActivated(
			org.drools.event.rule.RuleFlowGroupActivatedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void afterRuleFlowGroupDeactivated(
			org.drools.event.rule.RuleFlowGroupDeactivatedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void agendaGroupPopped(
			org.drools.event.rule.AgendaGroupPoppedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void agendaGroupPushed(
			org.drools.event.rule.AgendaGroupPushedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void beforeActivationFired(
			org.drools.event.rule.BeforeActivationFiredEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void beforeRuleFlowGroupActivated(
			org.drools.event.rule.RuleFlowGroupActivatedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

	public void beforeRuleFlowGroupDeactivated(
			org.drools.event.rule.RuleFlowGroupDeactivatedEvent arg0) {
		log.debug(arg0.toString());
		System.out.println("x");
		
	}

}
